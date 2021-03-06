
package watki.algorytmy;

import watki.TaskAcceptor;
import watki.TaskExecutor;

/**
 * Jednowątkowa wersja kolejki zadań
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
        // nic
    }
}
