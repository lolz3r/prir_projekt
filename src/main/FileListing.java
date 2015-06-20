
package main;

import executor.TaskAcceptor;
//import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;

/**
 * Recursive file listing optimized for fast performance and less memory usage (maximum on Java6, it could be
 * optimized for Java7).
 */
public class FileListing implements Runnable {
    //private final static Logger logger = Logger.getLogger(FileListing.class);
    private final File rootDirectory;
    private final TaskAcceptor<FileSearchBean> taskQueue;
    private final LinkedList<File> directoryQueue;

    public FileListing(File rootDirectory, TaskAcceptor<FileSearchBean> taskQueue) throws IllegalArgumentException {
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("ERR: rootDirectory should be directory");
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
                            //logger.warn("File listing interrupted", e);
                            Thread.currentThread().interrupt();
                        }
                    }
                    return false;
                }
            };
            while (directoryQueue.size() > 0) {
                final File rootNode = directoryQueue.removeFirst();
                //noinspection ResultOfMethodCallIgnored
                rootNode.listFiles(fileTraversalFilter);
            }
        } finally {
           // logger.info("Listing finished.");
            taskQueue.signalEndOfData();
        }
    }

    void pushSearchTask(File f) throws InterruptedException {
        taskQueue.push(new FileSearchBean(f));
    }
}
