package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.io.File;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.evaluation.TripleSource;

public class BEDTripleSource implements TripleSource {

	private File file;
	private ValueFactory vf;

	public BEDTripleSource(File file, ValueFactory vf) {
		this.file = file;
		this.vf = vf;
	}

	@Override
	public CloseableIteration<? extends Statement, QueryEvaluationException> getStatements(
			Resource subj, URI pred, Value obj, Resource... contexts)
			throws QueryEvaluationException {
		return new BEDFileFilterReader(file, subj, pred, obj, contexts,
				getValueFactory());
	}

	@Override
	public ValueFactory getValueFactory() {
		return vf;
	}

}
