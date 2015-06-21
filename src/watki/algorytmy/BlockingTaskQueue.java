

package watki.algorytmy;

import watki.TaskAcceptor;
import watki.TaskSupplier;

import java.util.LinkedList;

/**
 * kolejka zadań
 */

public class BlockingTaskQueue<T> implements TaskAcceptor<T>, TaskSupplier<T> {
    private final LinkedList<T> taskQueue;
    private final int maxSize;
    private boolean endOfData;

    /** Wielkość która zarządza blokowaniem  */
    private final Object sizeUpdateLock = new Object();
    private int currentSize = 0;

    /**
     * Twórz kolejkę blokująca za pomocą {@link Integer#MAX_VALUE} max_value - maksymalny rozmiar
     */
    public BlockingTaskQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Twórz kolejkę blokującą z podanym rozmiarem maksylanym
     *
     * @param maxSize maksymalny rozmiar
     * @throws IllegalArgumentException
     */
    public BlockingTaskQueue(int maxSize) throws IllegalArgumentException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize shall be greater than zero");
        }
        this.maxSize = maxSize;
        taskQueue = new LinkedList<T>();
    }

    //pobieranie, może zostać zablokowane jeśli rozmiar 0
    public T pull() throws InterruptedException {
        synchronized (taskQueue) {
            while (taskQueue.size() == 0 && !endOfData) {
                taskQueue.wait();
            }

            if (taskQueue.size() > 0) {
                T task = taskQueue.removeFirst();
                notifyUpdateSize(taskQueue.size());
                return task;
            }
        }
        Thread.currentThread().interrupt();
        return null;
    }

    //blokowane jeśli przekroczono maksylany rozmiar kolejki
    public void push(T task) throws IllegalArgumentException, InterruptedException {
        if (task == null) {
            throw new IllegalArgumentException("Task shall not be null");
        }

        boolean isAdded = false;
        while (!isAdded) {
            int currentSize;
            synchronized (taskQueue) {
                currentSize = taskQueue.size();
                if (currentSize < maxSize) {
                    isAdded = true;
                    taskQueue.addLast(task);
                    taskQueue.notify();
                }
            }

            if (!isAdded) {
                waitForSizeLowerThan(currentSize - maxSize / 2 + 1);
            }
        }
    }

    public void signalEndOfData() {
        synchronized (taskQueue) {
            endOfData = true;
            taskQueue.notifyAll();
        }
    }

    //aktualizu długość listy
    private void notifyUpdateSize(int newSize) {
        synchronized (sizeUpdateLock) {
            currentSize = newSize;
            sizeUpdateLock.notifyAll();
        }
    }

    /**
     * Czeka aż rozmiar listy zostanie zmieniony na podany rozmiar lub mniejszy
     *
     * @param expectedSize oczekiwany rozmiar
     * @throws InterruptedException
     */
    private void waitForSizeLowerThan(int expectedSize) throws InterruptedException {
        synchronized (sizeUpdateLock) {
            while (expectedSize < currentSize) {
                sizeUpdateLock.wait();
            }
        }
    }
}
