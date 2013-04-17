package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.CloseableIteratorIteration;
import info.aduna.iteration.EmptyIteration;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.UpdateExpr;
import org.openrdf.query.algebra.evaluation.EvaluationStrategy;
import org.openrdf.query.algebra.evaluation.impl.BindingAssigner;
import org.openrdf.query.algebra.evaluation.impl.CompareOptimizer;
import org.openrdf.query.algebra.evaluation.impl.ConjunctiveConstraintSplitter;
import org.openrdf.query.algebra.evaluation.impl.ConstantOptimizer;
import org.openrdf.query.algebra.evaluation.impl.DisjunctiveConstraintOptimizer;
import org.openrdf.query.algebra.evaluation.impl.FilterOptimizer;
import org.openrdf.query.algebra.evaluation.impl.IterativeEvaluationOptimizer;
import org.openrdf.query.algebra.evaluation.impl.OrderLimitOptimizer;
import org.openrdf.query.algebra.evaluation.impl.QueryModelNormalizer;
import org.openrdf.query.algebra.evaluation.impl.SameTermFilterOptimizer;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
//import org.openrdf.sail.UnknownSailTransactionStateException;
//import org.openrdf.sail.UpdateContext;

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

	}

	@Override
	public CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluate(
			TupleExpr tupleExpr, Dataset dataset, BindingSet bindings,
			boolean includeInferred) throws SailException {
		try {
			BEDTripleSource tripleSource = new BEDTripleSource(file, vf);
			EvaluationStrategy strategy = new OneLineAwareEvaluationStrategy(
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

			return strategy.evaluate(tupleExpr, EmptyBindingSet.getInstance());
		} catch (QueryEvaluationException e) {
			throw new SailException(e);
		}
	}

	@Override
	public CloseableIteration<? extends Resource, SailException> getContextIDs()
			throws SailException {
		return new EmptyIteration<Resource, SailException>();
	}

	@Override
	public CloseableIteration<? extends Statement, SailException> getStatements(
			Resource subj, URI pred, Value obj, boolean includeInferred,
			Resource... contexts) throws SailException {

		final BEDFileFilterReader bedFileFilterReader = new BEDFileFilterReader(
				file, subj, pred, obj, contexts, vf);
		return new CloseableIteratorIteration<Statement, SailException>() {

			@Override
			public boolean hasNext() throws SailException {
				try {
					return bedFileFilterReader.hasNext();
				} catch (QueryEvaluationException e) {
					throw new SailException(e);
				}
			}

			@Override
			public Statement next() throws SailException {
				try {
					return bedFileFilterReader.next();
				} catch (QueryEvaluationException e) {
					throw new SailException(e);
				}
			}

			@Override
			protected void handleClose() throws SailException {
				try {
					bedFileFilterReader.close();
				} catch (QueryEvaluationException e) {
					throw new SailException(e);
				}
				super.handleClose();
			}
		};

	}

	@Override
	public long size(Resource... contexts) throws SailException {
		for (Resource context : contexts)
			if (context == null) {
				final BEDFileFilterReader bedFileFilterReader = new BEDFileFilterReader(
						file, null, null, null, null, vf);
				long count = 0L;
				try {
					while (bedFileFilterReader.hasNext()) {
						bedFileFilterReader.next();
						count++;
					}
				} catch (QueryEvaluationException e) {
					throw new SailException(e);
				}
				return count;
			}
		return 0;
	}

//	@Override
//	public void begin() throws SailException {
//		throw new SailException("BED files can not be updated via SPARQL");
//	}
//
//	@Override
//	public void prepare() throws SailException {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void commit() throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");

	}

	@Override
	public void rollback() throws SailException {
		// TODO Auto-generated method stub

	}

//	@Override
//	public boolean isActive() throws UnknownSailTransactionStateException {
//		return false;
//	}

	@Override
	public void addStatement(Resource subj, URI pred, Value obj,
			Resource... contexts) throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");

	}

	@Override
	public void removeStatements(Resource subj, URI pred, Value obj,
			Resource... contexts) throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");

	}

//	@Override
//	public void startUpdate(UpdateContext op) throws SailException {
//		throw new SailException("BED files can not be updated via SPARQL");
//
//	}
//
//	@Override
//	public void addStatement(UpdateContext op, Resource subj, URI pred,
//			Value obj, Resource... contexts) throws SailException {
//		throw new SailException("BED files can not be updated via SPARQL");
//
//	}
//
//	@Override
//	public void removeStatement(UpdateContext op, Resource subj, URI pred,
//			Value obj, Resource... contexts) throws SailException {
//		throw new SailException("BED files can not be updated via SPARQL");
//
//	}
//
//	@Override
//	public void endUpdate(UpdateContext op) throws SailException {
//		throw new SailException("BED files can not be updated via SPARQL");
//
//	}

	@Override
	public void clear(Resource... contexts) throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");

	}

	@Override
	public CloseableIteration<? extends Namespace, SailException> getNamespaces()
			throws SailException {

		return new CloseableIteratorIteration<Namespace, SailException>() {
			private Iterator<Namespace> namespaces = Arrays.asList(
					new Namespace[] {
							new NamespaceImpl(FALDO.PREFIX, FALDO.NAMESPACE),
							new NamespaceImpl(BED.PREFIX, BED.NAMESPACE) })
					.iterator();

			@Override
			public boolean hasNext() throws SailException {
				return namespaces.hasNext();
			}

			@Override
			public Namespace next() throws SailException {
				return namespaces.next();
			};
		};
	}

	@Override
	public String getNamespace(String prefix) throws SailException {
		if (FALDO.PREFIX.equals(prefix))
			return FALDO.NAMESPACE;
		else if (BED.PREFIX.equals(prefix))
			return BED.NAMESPACE;
		return null;
	}

	@Override
	public void setNamespace(String prefix, String name) throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");

	}

	@Override
	public void removeNamespace(String prefix) throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");

	}

	@Override
	public void clearNamespaces() throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");

	}

	@Override
	public void executeUpdate(UpdateExpr arg0, Dataset arg1, BindingSet arg2,
			boolean arg3) throws SailException {
		throw new SailException("BED files can not be updated via SPARQL");
	}

}
