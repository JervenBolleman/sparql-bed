package ch.isbsib.sparql.bed;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class FALDO {
	public static final String PREFIX = "faldo";
	public static final String NAMESPACE = "http://biohackathon.org/resource/faldo#";

	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

	public static final URI BOTH_STRANDS_POSITION_CLASS;
	public static final URI EXACT_POSITION_CLASS;
	
	public static final URI FUZZY_POSITION_CLASS;
	public static final URI IN_BETWEEN_POSITION_CLASS;
	public static final URI IN_RANGE_POSITION_CLASS;
	public static final URI ONE_OF_POSITION_CLASS;
	public static final URI POSITION_CLASS;
	public static final URI REGION_CLASS;
	
	public static final URI FORWARD_STRAND_POSITION_CLASS;
	public static final URI REVERSE_STRANDED_POSITION_CLASS;
	public static final URI STRANDED_POSITION_CLASS;
	public static final URI AFTER_PREDICATE;
	public static final URI BEFORE_PREDICATE;
	public static final URI BEGIN_PREDICATE;
	public static final URI END_PREDICATE;
	public static final URI LOCATION_PREDICATE;
	public static final URI POSTION_PREDICATE;
	public static final URI REFERENCE_PREDICATE;

	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		// Properties common to Faldo
		BOTH_STRANDS_POSITION_CLASS = f.createURI(NAMESPACE, "BothStrandsPosition");
		EXACT_POSITION_CLASS = f.createURI(NAMESPACE, "ExactPosition");
		FORWARD_STRAND_POSITION_CLASS = f.createURI(NAMESPACE, "ForwardStrandPosition");
		FUZZY_POSITION_CLASS = f.createURI(NAMESPACE, "FuzzyPosition");
		IN_BETWEEN_POSITION_CLASS = f.createURI(NAMESPACE, "InBetweenPosition");
		IN_RANGE_POSITION_CLASS = f.createURI(NAMESPACE, "InRangePosition");
		ONE_OF_POSITION_CLASS = f.createURI(NAMESPACE, "OneOfPosition");
		POSITION_CLASS = f.createURI(NAMESPACE, "Position");
		REGION_CLASS = f.createURI(NAMESPACE, "Region");
		REVERSE_STRANDED_POSITION_CLASS = f.createURI(NAMESPACE, "ReverseStrandPosition");
		STRANDED_POSITION_CLASS = f.createURI(NAMESPACE, "StrandedPosition");
		AFTER_PREDICATE = f.createURI(NAMESPACE, "after");
		BEFORE_PREDICATE = f.createURI(NAMESPACE, "before");
		BEGIN_PREDICATE = f.createURI(NAMESPACE, "begin");
		END_PREDICATE = f.createURI(NAMESPACE, "end");
		LOCATION_PREDICATE = f.createURI(NAMESPACE, "location");
		POSTION_PREDICATE = f.createURI(NAMESPACE, "position");
		REFERENCE_PREDICATE = f.createURI(NAMESPACE, "reference");
	}
}
