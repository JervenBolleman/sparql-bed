package ch.isbsib.sparql.bed;

import java.io.File;

import org.openrdf.model.ValueFactory;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailBase;

public class BEDRepository extends SailBase {
	private Sail store;
	private File file;

	@Override
	public boolean isWritable() throws SailException {
		return store.isWritable();
	}

	@Override
	public ValueFactory getValueFactory() {
		return store.getValueFactory();
	}

	@Override
	protected void shutDownInternal() throws SailException {
		store.shutDown();
	}

	@Override
	protected SailConnection getConnectionInternal() throws SailException {
		return new BEDConnection(file, getValueFactory());
	}

	public void setSail(Sail sail) {
		this.store = sail;
	}

	public void setSamFile(File file){
		this.file =file;
	}
}
