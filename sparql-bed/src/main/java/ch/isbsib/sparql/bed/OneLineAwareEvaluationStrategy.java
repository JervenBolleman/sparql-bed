package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.util.Arrays;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.evaluation.impl.EvaluationStrategyImpl;

public class OneLineAwareEvaluationStrategy extends EvaluationStrategyImpl {

	public OneLineAwareEvaluationStrategy(BEDTripleSource tripleSource) {
		super(tripleSource);
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

	private List<URI> predicates = Arrays
			.asList(new URI[] { FALDO.POSTION_PREDICATE, FALDO.BEGIN_PREDICATE,
					FALDO.END_PREDICATE });

	private boolean allowAblePredicate(Var predicateVar) {
		for (URI pred : predicates) {
			if (pred.equals(predicateVar.getValue()))
				return true;
		}
		return false;
	}

}
