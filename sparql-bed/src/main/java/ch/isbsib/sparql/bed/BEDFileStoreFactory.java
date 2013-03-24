package ch.isbsib.sparql.bed;

import java.io.File;

import org.openrdf.sail.Sail;
import org.openrdf.sail.config.SailConfigException;
import org.openrdf.sail.config.SailFactory;
import org.openrdf.sail.config.SailImplConfig;

public class BEDFileStoreFactory implements SailFactory {

	/**
	 * The type of repositories that are created by this factory.
	 * 
	 * @see SailFactory#getSailType()
	 */
	public static final String SAIL_TYPE = "isbsib:BEDFileStore";

	/**
	 * Returns the Sail's type: <tt>openrdf:MemoryStore</tt>.
	 */
	public String getSailType() {
		return SAIL_TYPE;
	}

	public SailImplConfig getConfig() {
		return new BEDFileConfig();
	}

	public Sail getSail(SailImplConfig config)
		throws SailConfigException
	{
		if (!SAIL_TYPE.equals(config.getType())) {
			throw new SailConfigException("Invalid Sail type: " + config.getType());
		}

		BEDFileStore memoryStore = new BEDFileStore();

		if (config instanceof BEDFileConfig) {
			BEDFileConfig memConfig = (BEDFileConfig)config;

			memoryStore.setSamFile(new File(memConfig.getFile()));
		}

		return memoryStore;
	}

}
