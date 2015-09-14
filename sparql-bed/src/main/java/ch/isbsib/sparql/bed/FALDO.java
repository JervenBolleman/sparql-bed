package ch.isbsib.sparql.bed;

import org.openrdf.model.Namespace;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class FALDO {
	public static final String PREFIX = "faldo";
	public static final String NAMESPACE = "http://biohackathon.org/resource/faldo#";

	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

	public static final IRI BOTH_STRANDS_POSITION_CLASS;
	public static final IRI EXACT_POSITION_CLASS;
	
	public static final IRI FUZZY_POSITION_CLASS;
	public static final IRI IN_BETWEEN_POSITION_CLASS;
	public static final IRI IN_RANGE_POSITION_CLASS;
	public static final IRI ONE_OF_POSITION_CLASS;
	public static final IRI POSITION_CLASS;
	public static final IRI REGION_CLASS;
	
	public static final IRI FORWARD_STRAND_POSITION_CLASS;
	public static final IRI REVERSE_STRANDED_POSITION_CLASS;
	public static final IRI STRANDED_POSITION_CLASS;
	public static final IRI AFTER_PREDICATE;
	public static final IRI BEFORE_PREDICATE;
	public static final IRI BEGIN_PREDICATE;
	public static final IRI END_PREDICATE;
	public static final IRI LOCATION_PREDICATE;
	public static final IRI POSTION_PREDICATE;
	public static final IRI REFERENCE_PREDICATE;

	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		// Properties common to Faldo
		BOTH_STRANDS_POSITION_CLASS = f.createIRI(NAMESPACE, "BothStrandsPosition");
		EXACT_POSITION_CLASS = f.createIRI(NAMESPACE, "ExactPosition");
		FORWARD_STRAND_POSITION_CLASS = f.createIRI(NAMESPACE, "ForwardStrandPosition");
		FUZZY_POSITION_CLASS = f.createIRI(NAMESPACE, "FuzzyPosition");
		IN_BETWEEN_POSITION_CLASS = f.createIRI(NAMESPACE, "InBetweenPosition");
		IN_RANGE_POSITION_CLASS = f.createIRI(NAMESPACE, "InRangePosition");
		ONE_OF_POSITION_CLASS = f.createIRI(NAMESPACE, "OneOfPosition");
		POSITION_CLASS = f.createIRI(NAMESPACE, "Position");
		REGION_CLASS = f.createIRI(NAMESPACE, "Region");
		REVERSE_STRANDED_POSITION_CLASS = f.createIRI(NAMESPACE, "ReverseStrandPosition");
		STRANDED_POSITION_CLASS = f.createIRI(NAMESPACE, "StrandedPosition");
		AFTER_PREDICATE = f.createIRI(NAMESPACE, "after");
		BEFORE_PREDICATE = f.createIRI(NAMESPACE, "before");
		BEGIN_PREDICATE = f.createIRI(NAMESPACE, "begin");
		END_PREDICATE = f.createIRI(NAMESPACE, "end");
		LOCATION_PREDICATE = f.createIRI(NAMESPACE, "location");
		POSTION_PREDICATE = f.createIRI(NAMESPACE, "position");
		REFERENCE_PREDICATE = f.createIRI(NAMESPACE, "reference");
	}
}
