package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.QueryEvaluationException;

public class BEDFileFilterReader implements
		CloseableIteration<Statement, QueryEvaluationException> {
	private final BlockingQueue<Statement> statements;
	private final FilterReaderRunner runner;
	private Statement next;
	private boolean closed = false;
	private final static ExecutorService exec = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public BEDFileFilterReader(File samFile, Resource subj, URI pred,
			Value obj, Resource[] contexts, ValueFactory valueFactory) {
		statements = new ArrayBlockingQueue<Statement>(1000);
		runner = new FilterReaderRunner(samFile, subj, pred, obj, statements,
				valueFactory);
		exec.submit(runner);
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		next = null;
		while (!runner.done || !statements.isEmpty()) {
			try {
				next = statements.poll(1, TimeUnit.SECONDS);
				if (next != null) {
					return true;
				}
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
		return false;
	}

	@Override
	public Statement next() throws QueryEvaluationException {
		if (next == null)
			throw new NoSuchElementException("No more elements");
		if (closed)
			throw new IllegalStateException("Iterator has been closed");
		return next;
	}

	@Override
	public void remove() throws QueryEvaluationException {
		throw new QueryEvaluationException("Shizon does not support ");

	}

	@Override
	public void close() throws QueryEvaluationException {
		runner.done = true;
		closed = true;
	}
}
