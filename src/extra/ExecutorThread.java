
package extra;

import watki.algorytmy.TaskRunner;

/**
 * Prosta klasa do wykonywania zadań
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
