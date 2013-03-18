package ch.isbsib.sparql.bed;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
import org.openrdf.sail.memory.MemoryStore;

import ch.isbsib.sparql.bed.BED;
import ch.isbsib.sparql.bed.BEDRepository;
import ch.isbsib.sparql.bed.FALDO;

import junit.framework.TestCase;

public class BEDRepositoryTest extends TestCase {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private File newFile = null;
	private File dataDir = null;

	@Before
	public void setUp() {
		// try {
		newFile = new File(BEDRepositoryTest.class.getClassLoader()
				.getResource("example.bed").getFile());
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

	String query1 = "PREFIX bed:<" + BED.NAMESPACE
			+ "> SELECT DISTINCT ?read WHERE {?s bed:Chromosome ?read}";

	@Test
	public void testRecordNumber() throws IOException,
			QueryEvaluationException, MalformedQueryException,
			RepositoryException, SailException {

		assertTrue(newFile.exists());
		BEDRepository rep = new BEDRepository();
		rep.setDataDir(dataDir);
		rep.setSamFile(newFile);
		rep.setSail(new MemoryStore(dataDir));
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query1);
		TupleQueryResult eval = pTQ.evaluate();
		for (int i = 0; i < 1; i++) {
			assertTrue(eval.hasNext());
			assertNotNull(eval.next());
		}
		assertFalse(eval.hasNext());
	}

	String query2 = "PREFIX bed:<"
			+ BED.NAMESPACE
			+ "> SELECT (COUNT(?score) AS ?countScore) WHERE {?s bed:score ?score}";

	@Test
	public void testRecordNumberViaCount() throws IOException,
			QueryEvaluationException, MalformedQueryException,
			RepositoryException, SailException {

		assertTrue(newFile.exists());
		BEDRepository rep = new BEDRepository();
		rep.setDataDir(dataDir);
		rep.setSamFile(newFile);
		rep.setSail(new MemoryStore(dataDir));
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query2);
		TupleQueryResult eval = pTQ.evaluate();

		assertTrue(eval.hasNext());
		BindingSet next = eval.next();
		assertNotNull(next);
		assertEquals(next.getBinding("countScore").getValue().stringValue(),
				"9");
	}

	String query3 = "PREFIX bed:<"
			+ BED.NAMESPACE
			+ ">\n"
			+ "PREFIX rdf:<"
			+ RDF.NAMESPACE
			+ ">\n"
			+ "PREFIX faldo:<"
			+ FALDO.NAMESPACE
			+ ">\n"
			+ "SELECT (AVG(?length) as ?avgLength) \n"
			+ " WHERE {?s faldo:begin ?b ; faldo:end ?e . ?b faldo:position ?begin . ?e faldo:position ?end . BIND(abs(?end - ?begin) as ?length)} GROUP BY ?s";

	@Test
	public void testAverageReadLengthNumber() throws IOException,
			QueryEvaluationException, MalformedQueryException,
			RepositoryException, SailException {

		assertTrue(newFile.exists());
		BEDRepository rep = new BEDRepository();
		rep.setDataDir(dataDir);
		rep.setSamFile(newFile);
		rep.setSail(new MemoryStore(dataDir));
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query3);
		TupleQueryResult eval = pTQ.evaluate();

		assertTrue(eval.hasNext());
		BindingSet next = eval.next();
		assertNotNull(next);
		Binding lb = next.getBinding("avgLength");
		assertEquals("", "1166", lb.getValue()
				.stringValue());
	}
}
