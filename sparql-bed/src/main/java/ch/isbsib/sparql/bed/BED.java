package ch.isbsib.sparql.bed;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class BED {
	public static final String PREFIX = "faldo";
	public static final String NAMESPACE = "http://biohackathon.org/resource/bed#";
	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);
	
	public static final URI CHROMOSOME;
	public static final URI SCORE;
	public static final URI EXON;
	
	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		// Properties common to Faldo
		CHROMOSOME = f.createURI(NAMESPACE, "Chromosome");
		SCORE = f.createURI(NAMESPACE, "score");
		EXON = f.createURI(NAMESPACE, "exon");
	}
}
