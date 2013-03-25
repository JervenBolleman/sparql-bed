package ch.isbsib.sparql.bed;

import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.StatementPattern;

public class BEDFileBindingReader implements
		CloseableIteration<BindingSet, QueryEvaluationException> {

	private final BindingReaderRunner runner;
	private final static ExecutorService exec = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
				int a = 0;

				@Override
				public Thread newThread(Runnable arg0) {

					return new Thread(arg0, "BEDFileBindingReader" + (a++));
				}
			});
	private final BlockingQueue<BindingSet> queue;
	private final String wait = "wait";

	public BEDFileBindingReader(File file, BindingSet bindings, Join join,
			ValueFactory valueFactory) {
		StatementPattern left = (StatementPattern) join.getLeftArg();
		StatementPattern right = (StatementPattern) join.getRightArg();
		queue = new ArrayBlockingQueue<BindingSet>(1000);
		runner = new BindingReaderRunner(file, queue, left, right,
				valueFactory, bindings, wait);
		exec.submit(runner);
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		while (!runner.done) {
			if (queue.peek() != null)
				return true;
			else
				try {
					synchronized (wait) {
						wait.wait();
					}
				} catch (InterruptedException e) {
					Thread.interrupted();
					return hasNext();
				}
		}
		return !queue.isEmpty();
	}

	@Override
	public BindingSet next() throws QueryEvaluationException {
		return queue.poll();
	}

	@Override
	public void remove() throws QueryEvaluationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws QueryEvaluationException {
		runner.done = true;
	}

}
