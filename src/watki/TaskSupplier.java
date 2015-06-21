

package watki;

/**
 * Dodatkowy interfejs zadania
 */
public interface TaskSupplier<T> {
    /**
     * Wykonaj nowe zadanie
     *
     * @return nowe zadanie
     * @throws InterruptedException
     */
    T pull() throws InterruptedException;
}
