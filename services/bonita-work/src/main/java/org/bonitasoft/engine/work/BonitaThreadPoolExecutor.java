/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.work;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.bonitasoft.engine.commons.time.EngineClock;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;

/**
 * @author Julien Reboul
 * @author Baptiste Mesta
 */
public class BonitaThreadPoolExecutor extends ThreadPoolExecutor implements BonitaExecutorService {

    private final BlockingQueue<Runnable> workQueue;
    private final WorkFactory workFactory;
    private final TechnicalLoggerService logger;
    private final EngineClock engineClock;
    private final WorkExecutionCallback workExecutionCallback;

    public BonitaThreadPoolExecutor(final int corePoolSize,
            final int maximumPoolSize,
            final long keepAliveTime,
            final TimeUnit unit,
            final BlockingQueue<Runnable> workQueue,
            final ThreadFactory threadFactory,
            final RejectedExecutionHandler handler, WorkFactory workFactory, final TechnicalLoggerService logger,
            EngineClock engineClock, WorkExecutionCallback workExecutionCallback) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.workQueue = workQueue;
        this.workFactory = workFactory;
        this.logger = logger;
        this.engineClock = engineClock;
        this.workExecutionCallback = workExecutionCallback;
    }

    @Override
    public void clearAllQueues() {
        workQueue.clear();
    }

    @Override
    public Future<?> submit(final Runnable task) {
        // only submit if not shutdown
        if (!isShutdown()) {
            execute(task);
        }
        return null;
    }

    @Override
    public void shutdownAndEmptyQueue() {
        super.shutdown();
        logger.log(getClass(), TechnicalLogSeverity.INFO,
                "Clearing queue of work, had " + workQueue.size() + " elements");
        workQueue.clear();
    }

    @Override
    public void submit(WorkDescriptor work) {
        submit(() -> {
            if (work.getExecutionThreshold() != null && work.getExecutionThreshold().isAfter(engineClock.now())) {
                // Future implementation should use a real delay e.g. using a ScheduledThreadPoolExecutor
                // Will be executed later
                submit(work);
                return;
            }
            BonitaWork bonitaWork = workFactory.create(work);
            HashMap<String, Object> context = new HashMap<>();
            try {
                bonitaWork.work(context);
                workExecutionCallback.onSuccess(work);
            } catch (Exception e) {
                workExecutionCallback.onFailure(work, bonitaWork, context, e);
            }
        });
    }

}
