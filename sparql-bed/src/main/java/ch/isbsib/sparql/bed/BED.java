package ch.isbsib.sparql.bed;

import org.openrdf.model.IRI;
import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class BED {
	public static final String PREFIX = "faldo";
	public static final String NAMESPACE = "http://biohackathon.org/resource/bed#";
	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);
	
	public static final IRI CHROMOSOME;
	public static final IRI SCORE;
	public static final IRI EXON;
	public static final IRI FEATURE_CLASS;
	
	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		// Properties common to Faldo
		CHROMOSOME = f.createIRI(NAMESPACE, "Chromosome");
		SCORE = f.createIRI(NAMESPACE, "score");
		EXON = f.createIRI(NAMESPACE, "exon");
		FEATURE_CLASS= f.createIRI(NAMESPACE, "Feature");
	}
}
