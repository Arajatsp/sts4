/*******************************************************************************
 * Copyright (c) 2018 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.commons.languageserver.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.springframework.ide.vscode.commons.util.RunnableWithException;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class AsyncRunner {

	private static Scheduler executor = Schedulers.newSingle("STS4 Thread");

	// Used in test harness to wait for all pending request to finish.
	// We only need to remember the last request as requests are executed in order, so if
	// the last request is done, all requests are done
	private CompletableFuture<?> lastRequest;

	public AsyncRunner() {
	}

	public synchronized <T> CompletableFuture<T> invoke(Callable<T> callable) {
		CompletableFuture<T> x = Mono.fromCallable(callable).subscribeOn(executor).toFuture();
		lastRequest = x;
		return x;
	}

	public static void thenLog(Logger log, CompletableFuture<?> work) {
		work.handle((v, e) -> {
			if (e!=null) {
				log.error("", e);
			}
			return null;
		});
	}

	public synchronized void withLog(Logger logger, RunnableWithException runnable) {
		thenLog(logger, execute(runnable));
	}

	public synchronized CompletableFuture<Void> execute(RunnableWithException runnable) {
		CompletableFuture<Void> x = Mono.<Void>fromRunnable(() -> {
			try {
				runnable.run();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		})
		.subscribeOn(executor)
		.toFuture();
		lastRequest = x;
		return x;
	}


	public synchronized void waitForAll() {
		while (lastRequest != null) {
			try {
				lastRequest.get();
			} catch (Exception e) {

			}
			if (lastRequest.isDone()) {
				lastRequest = null;
			}
		}
	}

}
