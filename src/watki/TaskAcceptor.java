
package watki;

/**
 * interfejs wykonywania zadania
 */
public interface TaskAcceptor<T> {

    public void push(T task) throws IllegalArgumentException, InterruptedException;

    public void signalEndOfData();
}
