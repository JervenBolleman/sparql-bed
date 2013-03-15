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

	public static final URI BOTH_STRANDS_POSITION;
	public static final URI EXACT_POSITION;
	public static final URI FORWARD_STRAND_POSITION;
	public static final URI FUZZY_POSITION;
	public static final URI IN_BETWEEN_POSITION;
	public static final URI IN_RANGE_POSITION;
	public static final URI ONE_OF_POSITION;
	public static final URI POSITION_TYPE;
	public static final URI REGION;
	public static final URI REVERSE_STRAND_POSITION;
	public static final URI STRANDED_POSITION;
	public static final URI AFTER;
	public static final URI BEFORE;
	public static final URI BEGIN;
	public static final URI END;
	public static final URI LOCATION;
	public static final URI position;
	public static final URI REFERENCE;

	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		// Properties common to Faldo
		BOTH_STRANDS_POSITION = f.createURI(NAMESPACE, "BothStrandsPosition");
		EXACT_POSITION = f.createURI(NAMESPACE, "ExactPosition");
		FORWARD_STRAND_POSITION = f.createURI(NAMESPACE, "ForwardStrandPosition");
		FUZZY_POSITION = f.createURI(NAMESPACE, "FuzzyPosition");
		IN_BETWEEN_POSITION = f.createURI(NAMESPACE, "InBetweenPosition");
		IN_RANGE_POSITION = f.createURI(NAMESPACE, "InRangePosition");
		ONE_OF_POSITION = f.createURI(NAMESPACE, "OneOfPosition");
		POSITION_TYPE = f.createURI(NAMESPACE, "Position");
		REGION = f.createURI(NAMESPACE, "Region");
		REVERSE_STRAND_POSITION = f.createURI(NAMESPACE, "ReverseStrandPosition");
		STRANDED_POSITION = f.createURI(NAMESPACE, "StrandedPosition");
		AFTER = f.createURI(NAMESPACE, "after");
		BEFORE = f.createURI(NAMESPACE, "before");
		BEGIN = f.createURI(NAMESPACE, "begin");
		END = f.createURI(NAMESPACE, "end");
		LOCATION = f.createURI(NAMESPACE, "location");
		position = f.createURI(NAMESPACE, "position");
		REFERENCE = f.createURI(NAMESPACE, "reference");
	}
}
