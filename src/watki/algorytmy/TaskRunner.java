
package watki.algorytmy;

import watki.TaskExecutor;
import watki.TaskSupplier;

/**
 * Klasa wykonuwująca zadania
 * @param <T> typ zadania
 */
public class TaskRunner<T> implements Runnable {
    private final TaskSupplier<T> taskSupplier;
    private final TaskExecutor<T> taskExecutor;
    private volatile long threadUptime;
    private volatile long tasksProcessed;

    public TaskRunner(TaskSupplier<T> taskSupplier, TaskExecutor<T> taskExecutor) {
        this.taskSupplier = taskSupplier;
        this.taskExecutor = taskExecutor;
    }

    
    public void run() {
        tasksProcessed = 0;
        final long startTime = System.currentTimeMillis();
        Thread executorThread = Thread.currentThread();
        while (!executorThread.isInterrupted()) {
            try {
                final T t = taskSupplier.pull();
                if (t != null) {
                    taskExecutor.execute(t);
                    tasksProcessed++;
                }
            } catch (InterruptedException e) {
               
                Thread.currentThread().interrupt();
            } catch (Exception e) {
            }
        }

        threadUptime = System.currentTimeMillis() - startTime;

    }

    public long getTasksProcessed() {
        return tasksProcessed;
    }

    public long getThreadUptime() {
        return threadUptime;
    }
}
