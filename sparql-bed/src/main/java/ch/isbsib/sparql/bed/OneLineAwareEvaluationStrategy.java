package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.util.Arrays;
import java.util.List;

import org.openrdf.model.IRI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.evaluation.federation.FederatedServiceResolverImpl;
import org.openrdf.query.algebra.evaluation.impl.SimpleEvaluationStrategy;

public class OneLineAwareEvaluationStrategy extends SimpleEvaluationStrategy  {

	public OneLineAwareEvaluationStrategy(BEDTripleSource tripleSource) {
		super(tripleSource, new FederatedServiceResolverImpl());
	}

	@Override
	public CloseableIteration<BindingSet, QueryEvaluationException> evaluate(
			Join join, BindingSet bindings) throws QueryEvaluationException {
		if (oneLine(join)) {
			return ((BEDTripleSource) tripleSource).getStatements(bindings,
					join);
		} else
			return super.evaluate(join, bindings);
	}

	private boolean oneLine(Join join) throws QueryEvaluationException {
		TupleExpr la = join.getLeftArg();
		TupleExpr ra = join.getRightArg();
		if (la instanceof StatementPattern && ra instanceof StatementPattern) {
			StatementPattern lp = (StatementPattern) la;
			StatementPattern rp = (StatementPattern) ra;
			if (lp.getSubjectVar().equals(rp.getSubjectVar())
					&& allowAblePredicate(lp.getPredicateVar())
					&& allowAblePredicate(rp.getPredicateVar()))
				return true;
		}
		return false;
	}

	private List<IRI> predicates = Arrays
			.asList(new IRI[] { FALDO.POSTION_PREDICATE, FALDO.BEGIN_PREDICATE,
					FALDO.END_PREDICATE });

	private boolean allowAblePredicate(Var predicateVar) {
		for (IRI pred : predicates) {
			if (pred.equals(predicateVar.getValue()))
				return true;
		}
		return false;
	}

}
