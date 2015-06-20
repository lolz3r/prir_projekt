
package executor.impl;

import main.FileSearchBean;
import executor.TaskAcceptor;
import executor.TaskExecutor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Simple Naive substring pattern search algorithm implementation.
 * Complexity: O(nm),
 *  where m - length of substring,
 *  n - length of the searchable text.
 */
public class NaiveFileSearchTaskExecutor implements TaskExecutor<FileSearchBean> {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private final byte[] patternBytes;
    private final TaskAcceptor<FileSearchBean> resultCollector;
    private final int bufferSize;

    public NaiveFileSearchTaskExecutor(byte[] patternBytes, TaskAcceptor<FileSearchBean> resultCollector, int bufferSize) {
        this.resultCollector = resultCollector;
        this.patternBytes = patternBytes;
        this.bufferSize = bufferSize;

        if (bufferSize < patternBytes.length) {
            throw new IllegalArgumentException("bufferSize can't be smaller than pattern length");
        }
    }

    public NaiveFileSearchTaskExecutor(byte[] patternBytes, TaskAcceptor<FileSearchBean> resultCollector) {
        this(patternBytes, resultCollector, DEFAULT_BUFFER_SIZE);
    }

    
    public void execute(FileSearchBean task) throws Exception {
        final FileInputStream fileInputStream = new FileInputStream(task.getInputFile());
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, bufferSize);

        try {
            // Naive substring matching algorithm
            int buff;
            while ((buff = bufferedInputStream.read()) != -1) {
                if (buff == (patternBytes[0] & 0xff)) {
                    bufferedInputStream.mark(patternBytes.length);
                    int k = 1;
                    while (k < patternBytes.length
                            && (buff = bufferedInputStream.read()) != -1
                            && (patternBytes[k] & 0xff) == buff) {
                        k++;
                    }

                    if (k == patternBytes.length) {
                        // We found whole pattern
                        resultCollector.push(task);
                        break;
                    }
                    bufferedInputStream.reset();
                }
            }
        } finally {
            try {
                bufferedInputStream.close();
            } catch (IOException ioe) { /* ignore silently */ }
            try {
                fileInputStream.close();
            } catch(IOException ioe) { /* ignore silently */ }
        }
    }
}
