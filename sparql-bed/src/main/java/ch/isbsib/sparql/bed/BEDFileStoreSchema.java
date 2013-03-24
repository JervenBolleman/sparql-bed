package ch.isbsib.sparql.bed;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class BEDFileStoreSchema {
	/** The BEDFileStore schema namespace (<tt>http://www..org/config/sail/memory#</tt>). */
	public static final String NAMESPACE = "https://github.com/JervenBolleman/sparql-bed/config/sail/bedfile#";

	public final static URI FILE;

		static {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		FILE = factory.createURI(NAMESPACE, "file");
	}
}
