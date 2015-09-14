package ch.isbsib.sparql.bed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.Feature;
import org.broad.tribble.bed.BEDCodec;
import org.broad.tribble.bed.BEDFeature;
import org.broad.tribble.readers.LineIterator;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FilterReaderRunner implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(FilterReaderRunner.class);
	private final BlockingQueue<Statement> statements;
	private final Resource subj;
	private final Value obj;
	private final IRI pred;
	private final AbstractFeatureReader<BEDFeature, LineIterator> reader;
	volatile boolean done = false;
	private final File bedFile;
	private final ValueFactory vf;
//	private final Pattern comma = Pattern.compile(",");

	public FilterReaderRunner(File bedFile, Resource subj, IRI pred, Value obj,
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
		Iterable<BEDFeature> iter;
		try {
			BEDToTripleConverter conv = new BEDToTripleConverter(vf, pred);
			iter = reader.iterator();
			for (Feature feature : iter) {
				for (Statement statement : filter(conv.convertLineToTriples(
						filePath, feature, lineNo++))) {
					try {
						while (!offer(statement) && !this.done)
							;

					} catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
			}
		} catch (IOException e) {
			log.error("IO error while reading bed file", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				log.error("IO error while closing bed file", e);
			}
			done = true;
		}
	}

	protected boolean offer(Statement statement) throws InterruptedException {
		boolean offer = statements.offer(statement, 100, TimeUnit.MICROSECONDS);
		return offer;
	}

	private List<Statement> filter(List<Statement> statements) {
		List<Statement> filtered = new ArrayList<Statement>();
		for (Statement toFilter : statements) {
			Resource subject = toFilter.getSubject();
			Resource predicate = toFilter.getPredicate();
			Value object = toFilter.getObject();
			if (matches(subject, subj) && matches(predicate, pred)
					&& matches(object, obj)) {
				filtered.add(toFilter);
			}
		}
		return filtered;
	}

	protected boolean matches(Value subject, Value subj) {
		return subject.equals(subj) || subj == null;
	}

}