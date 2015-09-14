package ch.isbsib.sparql.bed;

import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;

public class BEDFileStoreSchema {
	/** The BEDFileStore schema namespace (<tt>http://www..org/config/sail/memory#</tt>). */
	public static final String NAMESPACE = "https://github.com/JervenBolleman/sparql-bed/config/sail/bedfile#";

	public final static IRI FILE;

		static {
		ValueFactory factory = new SimpleValueFactory();
		FILE = factory.createIRI(NAMESPACE, "file");
	}
}
