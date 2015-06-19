/*
 * Copyright 2011 Andrew Porokhin. All rights reserved.
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */

package executor.impl;

import executor.TaskAcceptor;
import executor.TaskExecutor;

/**
 * Single-thread approach
 */
public class SingleTaskQueue<T> implements TaskAcceptor<T> {
    private final TaskExecutor<T> taskExecutor;

    public SingleTaskQueue(TaskExecutor<T> taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    
    public void push(T task) throws IllegalArgumentException, InterruptedException {
        try {
            taskExecutor.execute(task);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    
    public void signalEndOfData() {
        // Do nothing
    }
}
