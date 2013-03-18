package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.Feature;
import org.broad.tribble.annotation.Strand;
import org.broad.tribble.bed.BEDCodec;
import org.broad.tribble.bed.BEDFeature;
import org.broad.tribble.bed.FullBEDFeature.Exon;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.QueryEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BEDFileFilterReader implements
		CloseableIteration<Statement, QueryEvaluationException> {
	private final BlockingQueue<Statement> statements;
	private final ReaderRunner runner;
	private static final String wait = "wait";
	private final static ExecutorService exec = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public BEDFileFilterReader(File samFile, Resource subj, URI pred,
			Value obj, Resource[] contexts, ValueFactory valueFactory) {
		statements = new LinkedBlockingQueue<Statement>();
		runner = new ReaderRunner(samFile, subj, pred, obj, statements,
				valueFactory);
		exec.submit(runner);
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		while (!runner.done) {
			if (!statements.isEmpty())
				return true;
			else
				try {
					synchronized (wait) {
						wait.wait();
					}
				} catch (InterruptedException e) {
					Thread.interrupted();
					return hasNext();
				}
		}
		return !statements.isEmpty();
	}

	@Override
	public Statement next() throws QueryEvaluationException {
		return statements.poll();
	}

	@Override
	public void remove() throws QueryEvaluationException {
		throw new QueryEvaluationException("Shizon does not support ");

	}

	@Override
	public void close() throws QueryEvaluationException {
		runner.done = true;

	}

	private static class ReaderRunner implements Runnable {
		private static final Logger log = LoggerFactory
				.getLogger(ReaderRunner.class);
		private final BlockingQueue<Statement> statements;
		private final Resource subj;
		private final Value obj;
		private final URI pred;
		private final AbstractFeatureReader reader;
		private volatile boolean done = false;
		private final File bedFile;
		private final ValueFactory vf;
		private final Pattern comma = Pattern.compile(",");

		public ReaderRunner(File bedFile, Resource subj, URI pred, Value obj,
				BlockingQueue<Statement> statements, ValueFactory vf) {

			this.vf = vf;

			this.reader = AbstractFeatureReader.getFeatureReader(
					bedFile.getAbsolutePath(), new BEDCodec(), false);

			this.subj = subj;
			this.pred = pred;
			this.obj = obj;
			this.statements = statements;
			this.bedFile = bedFile;
		}

		@Override
		public void run() {
			long lineNo = 0;
			String filePath = "file:///" + bedFile.getAbsolutePath();
			Iterable<Feature> iter;
			try {
				iter = reader.iterator();
				for (Feature feature : iter) {
					convertLineToTriples(filePath, feature, lineNo++);
				}
			} catch (IOException e) {
				log.error("IO error while reading bed file", e);
			} finally {

				done = true;
				synchronized (wait) {
					wait.notifyAll();
				}
			}
		}

		protected void convertLineToTriples(String filePath, Feature feature,
				long lineNo) {
			String recordPath = filePath + '/' + lineNo;
			URI recordId = vf.createURI(recordPath);
			add(recordId, BED.CHROMOSOME, feature.getChr());
			add(recordId, RDF.TYPE, BED.FEATURE_CLASS);
			add(recordId, RDF.TYPE, FALDO.REGION_CLASS);
			URI alignStartId = vf.createURI(recordPath + "#start");
			add(recordId, FALDO.BEGIN_PREDICATE, alignStartId);
			add(alignStartId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
			add(alignStartId, FALDO.POSTION_PREDICATE, feature.getStart());
			add(alignStartId, FALDO.REFERENCE_PREDICATE, feature.getChr());
			URI alignEndId = vf.createURI(recordPath + "#end");
			add(recordId, FALDO.END_PREDICATE, alignEndId);
			add(alignEndId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
			add(alignEndId, FALDO.POSTION_PREDICATE, feature.getEnd());
			add(alignEndId, FALDO.REFERENCE_PREDICATE, feature.getChr());
			if (feature instanceof BEDFeature) {
				convertLineToTriples(filePath, (BEDFeature) feature, lineNo);
			}
		}

		protected void convertLineToTriples(String filePath,
				BEDFeature feature, long lineNo) {
			String recordPath = filePath + '/' + lineNo;
			URI recordId = vf.createURI(recordPath);
			if (feature.getName() != null) // name
				add(recordId, RDFS.LABEL, feature.getName());
			if (feature.getScore() != Float.NaN) // score
				add(recordId, BED.SCORE, feature.getScore());
			addStrandedNessInformation(feature, recordId);
			// we skip position 678 as these are colouring instructions

			for (Exon exon : feature.getExons()) {
				
				String exonPath = recordPath + "/exon/" + exon.getNumber();
				URI exonId = vf.createURI(exonPath);
				URI beginId = vf.createURI(exonPath + "/begin");
				URI endId = vf.createURI(exonPath + "/end");
				add(recordId, BED.EXON, endId);
				add(exonId, RDF.TYPE, FALDO.REGION_CLASS);
				add(exonId, FALDO.BEGIN_PREDICATE, beginId);
				add(beginId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
				add(beginId, FALDO.POSTION_PREDICATE, exon.getCdStart());
				add(beginId, FALDO.REFERENCE_PREDICATE, feature.getChr());
				add(exonId, FALDO.END_PREDICATE, endId);
				add(endId, RDF.TYPE, FALDO.EXACT_POSITION_CLASS);
				add(endId, FALDO.POSTION_PREDICATE, exon.getCdEnd());
				add(endId, FALDO.REFERENCE_PREDICATE, feature.getChr());
			}

		}

		protected void addStrandedNessInformation(BEDFeature feature,
				URI alignEndId) {

			if (Strand.POSITIVE == feature.getStrand()) {
				add(alignEndId, RDF.TYPE, FALDO.FORWARD_STRAND_POSITION_CLASS);
			} else if (Strand.NEGATIVE == feature.getStrand()) {
				add(alignEndId, RDF.TYPE, FALDO.REVERSE_STRANDED_POSITION_CLASS);
			} else {
				add(alignEndId, RDF.TYPE, FALDO.STRANDED_POSITION_CLASS);
			}

		}

		private void add(URI subject, URI predicate, String string) {
			add(subject, predicate, vf.createLiteral(string));

		}

		private void add(URI subject, URI predicate, int string) {
			add(subject, predicate, vf.createLiteral(string));

		}

		private void add(URI subject, URI predicate, float string) {
			add(subject, predicate, vf.createLiteral(string));

		}

		private void add(Resource subject, URI predicate, Value object) {
			try {
				if (matches(subject, subj) && matches(predicate, pred)
						&& matches(object, obj)) {
					statements
							.put(new StatementImpl(subject, predicate, object));
					synchronized (wait) {
						wait.notifyAll();
					}
				}
			} catch (InterruptedException e) {
				Thread.interrupted();
			}

		}

		protected boolean matches(Value subject, Value subj) {
			return subject.equals(subj) || subj == null;
		}

	}
}
