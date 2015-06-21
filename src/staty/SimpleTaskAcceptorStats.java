
package staty;

import watki.TaskAcceptor;

/**
 * Dodatek do tworzenia statystyk
 */
public class SimpleTaskAcceptorStats<T> implements TaskAcceptor<T> {
    private final TaskAcceptor<T> taskAcceptor;
    private long taskCount;
    private long constructionTime;
    private long totalWaitTime;

    public SimpleTaskAcceptorStats(TaskAcceptor<T> taskAcceptor) {
        this.taskAcceptor = taskAcceptor;
        taskCount = 0;
        totalWaitTime = 0;
        constructionTime = System.currentTimeMillis();
    }

    
    public void push(T task) throws IllegalArgumentException, InterruptedException {
        taskCount++;
        taskAcceptor.push(task);
    }

    
    public void signalEndOfData() {
        taskAcceptor.signalEndOfData();
    }

    public long getTaskCount() {
        return taskCount;
    }

    public long getConstructionTime() {
        return constructionTime;
    }

    public long getTotalWaitTime() {
        return totalWaitTime;
    }
}
