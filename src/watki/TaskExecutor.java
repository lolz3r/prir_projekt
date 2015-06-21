

package watki;

/**
 * Interfejs wykonujący zadanie
 */
public interface TaskExecutor<T> {
    /**
     * Wykonaj zadanie
     * @param task zadanie
     * @throws Exception
     */
    void execute(T task) throws Exception;
}
