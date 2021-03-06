package ch.isbsib.sparql.bed;

import java.io.File;

import org.openrdf.model.ValueFactory;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.AbstractSail;

public class BEDFileStore extends AbstractSail {
	private File file;
	private ValueFactory vf;

	@Override
	public boolean isWritable() throws SailException {
		return false;
	}

	@Override
	public ValueFactory getValueFactory() {
		return vf;
	}

	@Override
	protected void shutDownInternal() throws SailException {

	}

	@Override
	protected SailConnection getConnectionInternal() throws SailException {
		return new BEDConnection(file, getValueFactory());
	}

	public void setValueFactory(ValueFactory vf) {
		this.vf = vf;
	}

	public void setBedFile(File file){
		this.file =file;
	}
}
