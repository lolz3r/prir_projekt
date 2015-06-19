/*
 * Copyright 2011 Andrew Porokhin. All rights reserved.
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */

package stats;

import executor.TaskAcceptor;
import executor.TaskSupplier;

/**
 * Task queue statistics decorator.
 */
public class TaskQueueStatistics<T> implements TaskAcceptor<T>, TaskSupplier<T> {
    private final TaskAcceptor<T> taskAcceptor;
    private final TaskSupplier<T> taskSupplier;
    private long queueLength;
    private long maxQueueLength;

    public TaskQueueStatistics(TaskAcceptor<T> taskAcceptor, TaskSupplier<T> taskSupplier) {
        this.taskAcceptor = taskAcceptor;
        this.taskSupplier = taskSupplier;
    }

    public synchronized void updateLength(int delta) {
        queueLength += delta;

        if (queueLength > maxQueueLength) {
            maxQueueLength = queueLength;
        }
    }

    public long getQueueLength() {
        return queueLength;
    }

    public long getMaxQueueLength() {
        return maxQueueLength;
    }

    
    public void push(T task) throws IllegalArgumentException, InterruptedException {
        updateLength(1);
        taskAcceptor.push(task);
    }

    
    public void signalEndOfData() {
        taskAcceptor.signalEndOfData();
    }

    
    public T pull() throws InterruptedException {
        updateLength(-1);
        return taskSupplier.pull();
    }
}
