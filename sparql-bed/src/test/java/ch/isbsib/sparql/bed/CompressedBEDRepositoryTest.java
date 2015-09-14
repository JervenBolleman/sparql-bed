package ch.isbsib.sparql.bed;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.SailException;

public class CompressedBEDRepositoryTest extends BEDRepositoryTest {


	@Override
	@Before
	public void setUp() {
		// try {
		newFile = new File(CompressedBEDRepositoryTest.class.getClassLoader()
				.getResource("example.sorted.bed.gz").getFile());
		dataDir = folder.newFolder("data.dir");
		//
		// } catch (IOException e) {
		// fail();
		// }
	}

	@After
	public void tearDown() {

		dataDir.delete();
	}

	
}
