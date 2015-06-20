
package util;

import executor.impl.TaskRunner;

/**
 * Simple thread executor class which provides its task runner.
 */
public class ExecutorThread<T> extends Thread {
    private final TaskRunner<T> taskRunner;
    public ExecutorThread(TaskRunner<T> taskRunner, String s) {
        super(taskRunner, s);
        this.taskRunner = taskRunner;
    }

    public TaskRunner<T> getTaskRunner() {
        return taskRunner;
    }
}
