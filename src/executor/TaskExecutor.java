

package executor;

/**
 * Executor for task.
 * @param <T> Type of tasks for this executor.
 */
public interface TaskExecutor<T> {
    /**
     * Execute task of specified type.
     * @param task Task
     * @throws Exception if any error occurs
     */
    void execute(T task) throws Exception;
}
