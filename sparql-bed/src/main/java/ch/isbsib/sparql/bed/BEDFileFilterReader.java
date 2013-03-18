package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

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
		private final BEDFileReader reader;
		private volatile boolean done = false;
		private final File bedFile;
		private final ValueFactory vf;
		private final Pattern comma = Pattern.compile(",");
		
		public ReaderRunner(File bedFile, Resource subj, URI pred, Value obj,
				BlockingQueue<Statement> statements, ValueFactory vf) {

			this.vf = vf;
			try {
				this.reader = new BEDFileReader(bedFile);
			} catch (FileNotFoundException e) {
				done = true;
				log.error("Can't find bed file to work on");
				throw new RuntimeException(e);
			} catch (IOException e) {
				done = true;
				log.error("Can't find bed file to work on");
				throw new RuntimeException(e);
			}
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
			while (reader.hasNext()) {
				convertLineToTriples(filePath, reader.next(), lineNo++);
			}
			done = true;
			synchronized (wait) {
				wait.notifyAll();
			}
		}

		protected void convertLineToTriples(String filePath, String[] record,
				long lineNo) {
			String recordPath = filePath + '/' + lineNo;
			URI recordId = vf.createURI(recordPath);
			add(recordId, BED.CHROMOSOME, record[0]);

			URI alignStartId = vf.createURI(recordPath + "start");
			add(recordId, FALDO.BEGIN, alignStartId);
			add(alignStartId, RDF.TYPE, FALDO.EXACT_POSITION);
			add(alignStartId, FALDO.position, Long.parseLong(record[1]));

			URI alignEndId = vf.createURI(recordPath + "end");
			add(recordId, FALDO.END, alignEndId);
			add(alignEndId, RDF.TYPE, FALDO.EXACT_POSITION);
			add(alignEndId, FALDO.position, Long.parseLong(record[2]));

			if (record[3] != null) // name
				add(recordId, RDFS.LABEL, record[3]);
			if (record[4] != null) // score
				add(recordId, BED.SCORE, record[4]);
			addStrandedNessInformation(record, alignEndId);
			// we skip position 678 as these are colouring instructions
			int blockCount = 0;
			URI[] blockids = null;
			if (record[9] != null) {// block/exon count
				blockCount = Integer.parseInt(record[9]);
				blockids = new URI[blockCount];
				for (int a = 0; a < blockCount; a++) {
					blockids[a] = vf.createURI(recordPath + "/block/" + a);
					add(recordId, BED.EXON, blockids[a]);
				}
			}
			String[] sizes = null;
			if (record[10] != null) {
				sizes = comma.split(record[10]);

			}
			if (record[11] != null) {
				String[] begin = comma.split(record[11]);
				for (int i = 0; i < sizes.length && i < blockCount; i++) {
					String size = sizes[i];
					URI blockId = blockids[i];
					URI beginId = vf.createURI(blockId.getNamespace(),
							blockId.getLocalName() + "/begin");
					URI endId = vf.createURI(blockId.getNamespace(),
							blockId.getLocalName() + "/begin");
					add(blockId, RDF.TYPE, FALDO.REGION);
					add(blockId, FALDO.BEGIN, beginId);
					add(beginId, RDF.TYPE, FALDO.EXACT_POSITION);
					long beginPos = Long.parseLong(begin[i]);
					add(beginId, FALDO.position, beginPos);
					add(blockId, FALDO.END, endId);
					add(endId, RDF.TYPE, FALDO.EXACT_POSITION);
					add(endId, FALDO.position, beginPos + Long.parseLong(size));
				}
			}
		}

		protected void addStrandedNessInformation(String[] record,
				URI alignEndId) {
			if (record[5] != null) // strand
			{
				if ("+".equals(record[5])) {
					add(alignEndId, RDF.TYPE, FALDO.FORWARD_STRAND_POSITION);
				} else if ("-".equals(record[5])) {
					add(alignEndId, RDF.TYPE, FALDO.REVERSE_STRAND_POSITION);
				} else {
					add(alignEndId, RDF.TYPE, FALDO.STRANDED_POSITION);
				}
			}
		}

		private void add(URI subject, URI predicate, String string) {
			add(subject, predicate, vf.createLiteral(string));

		}

		private void add(URI subject, URI predicate, long string) {
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
