package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.io.File;

import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.evaluation.EvaluationStrategy;
import org.openrdf.query.algebra.evaluation.TripleSource;
import org.openrdf.query.algebra.evaluation.impl.BindingAssigner;
import org.openrdf.query.algebra.evaluation.impl.CompareOptimizer;
import org.openrdf.query.algebra.evaluation.impl.ConjunctiveConstraintSplitter;
import org.openrdf.query.algebra.evaluation.impl.ConstantOptimizer;
import org.openrdf.query.algebra.evaluation.impl.DisjunctiveConstraintOptimizer;
import org.openrdf.query.algebra.evaluation.impl.EvaluationStrategyImpl;
import org.openrdf.query.algebra.evaluation.impl.FilterOptimizer;
import org.openrdf.query.algebra.evaluation.impl.IterativeEvaluationOptimizer;
import org.openrdf.query.algebra.evaluation.impl.OrderLimitOptimizer;
import org.openrdf.query.algebra.evaluation.impl.QueryModelNormalizer;
import org.openrdf.query.algebra.evaluation.impl.SameTermFilterOptimizer;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.UnknownSailTransactionStateException;
import org.openrdf.sail.UpdateContext;

public class BEDConnection implements SailConnection {
	private final File file;
	private final ValueFactory vf;

	public BEDConnection(File file, ValueFactory vf) {
		super();
		this.file = file;
		this.vf = vf;
	}

	@Override
	public boolean isOpen() throws SailException {
		return true;
	}

	@Override
	public void close() throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluate(
			TupleExpr tupleExpr, Dataset dataset, BindingSet bindings,
			boolean includeInferred) throws SailException {
		try {
			TripleSource tripleSource = new BEDTripleSource(file, vf);
			EvaluationStrategy strategy = new EvaluationStrategyImpl(
					tripleSource);
			tupleExpr = tupleExpr.clone();
			new BindingAssigner().optimize(tupleExpr, dataset, bindings);
			new ConstantOptimizer(strategy).optimize(tupleExpr, dataset,
					bindings);
			new CompareOptimizer().optimize(tupleExpr, dataset, bindings);
			new ConjunctiveConstraintSplitter().optimize(tupleExpr, dataset,
					bindings);
			new DisjunctiveConstraintOptimizer().optimize(tupleExpr, dataset,
					bindings);
			new SameTermFilterOptimizer()
					.optimize(tupleExpr, dataset, bindings);
			new QueryModelNormalizer().optimize(tupleExpr, dataset, bindings);

			// new SubSelectJoinOptimizer().optimize(tupleExpr, dataset,
			// bindings);
			new IterativeEvaluationOptimizer().optimize(tupleExpr, dataset,
					bindings);
			new FilterOptimizer().optimize(tupleExpr, dataset, bindings);
			new OrderLimitOptimizer().optimize(tupleExpr, dataset, bindings);

			CloseableIteration<BindingSet, QueryEvaluationException> iter;
			return strategy.evaluate(tupleExpr, EmptyBindingSet.getInstance());
		} catch (QueryEvaluationException e) {
			throw new SailException(e);
		}
	}

	@Override
	public CloseableIteration<? extends Resource, SailException> getContextIDs()
			throws SailException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CloseableIteration<? extends Statement, SailException> getStatements(
			Resource subj, URI pred, Value obj, boolean includeInferred,
			Resource... contexts) throws SailException {

		return null;
	}

	@Override
	public long size(Resource... contexts) throws SailException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void begin() throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare() throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit() throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive() throws UnknownSailTransactionStateException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addStatement(Resource subj, URI pred, Value obj,
			Resource... contexts) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeStatements(Resource subj, URI pred, Value obj,
			Resource... contexts) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startUpdate(UpdateContext op) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addStatement(UpdateContext op, Resource subj, URI pred,
			Value obj, Resource... contexts) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeStatement(UpdateContext op, Resource subj, URI pred,
			Value obj, Resource... contexts) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endUpdate(UpdateContext op) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear(Resource... contexts) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public CloseableIteration<? extends Namespace, SailException> getNamespaces()
			throws SailException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace(String prefix) throws SailException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNamespace(String prefix, String name) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeNamespace(String prefix) throws SailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearNamespaces() throws SailException {
		// TODO Auto-generated method stub

	}

}
