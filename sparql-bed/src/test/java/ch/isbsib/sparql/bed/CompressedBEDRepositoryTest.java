package ch.isbsib.sparql.bed;

import java.io.File;

import org.junit.After;
import org.junit.Before;

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
