
package main;

import watki.TaskAcceptor;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;

/**
 * Rekurencyjne wyszukuje pliki w katalogu i wszystkich podkatalogach
 */
public class FileListing implements Runnable {
    private final File rootDirectory;
    private final TaskAcceptor<FileSearchBean> taskQueue;
    private final LinkedList<File> directoryQueue;

    public FileListing(File rootDirectory, TaskAcceptor<FileSearchBean> taskQueue) throws IllegalArgumentException {
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Błąd, ścieżka nie jest folderem!");
        }

        this.rootDirectory = rootDirectory;
        this.taskQueue = taskQueue;
        directoryQueue = new LinkedList<File>();
    }

    public void run() {
        try {
            directoryQueue.addLast(rootDirectory);

            final FileFilter fileTraversalFilter = new FileFilter() {
                public boolean accept(File pathName) {
                    if (pathName.isDirectory()) {
                        directoryQueue.add(pathName);
                    } else if (pathName.isFile()) {
                        try {
                            taskQueue.push(new FileSearchBean(pathName));
                        } catch (InterruptedException e) {
                            //logger.warn("przewane, błąd", e);
                            Thread.currentThread().interrupt();
                        }
                    }
                    return false;
                }
            };
            while (directoryQueue.size() > 0) {
                final File rootNode = directoryQueue.removeFirst();
                rootNode.listFiles(fileTraversalFilter);
            }
        } finally {
           // logger.info("zako�czono wyszukiwanie.");
            taskQueue.signalEndOfData();
        }
    }

    void pushSearchTask(File f) throws InterruptedException {
        taskQueue.push(new FileSearchBean(f));
    }
}
