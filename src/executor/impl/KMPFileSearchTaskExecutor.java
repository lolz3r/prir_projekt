
package executor.impl;

import main.FileSearchBean;
import executor.TaskAcceptor;
import executor.TaskExecutor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * KMP algorithm substring pattern searching algorithm implementation.
 * Complexity: O(m)+O(n)
 *  where m - length of substring,
 *  n - length of the searchable text.
 */
public class KMPFileSearchTaskExecutor implements TaskExecutor<FileSearchBean> {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private final byte[] patternBytes;
    private final int[] kmpNext;
    private final int bufferSize;
    private final TaskAcceptor<FileSearchBean> resultCollector;

    public KMPFileSearchTaskExecutor(byte[] patternBytes, TaskAcceptor<FileSearchBean> resultCollector) {
        this(patternBytes, resultCollector, DEFAULT_BUFFER_SIZE);
    }

    public KMPFileSearchTaskExecutor(byte[] patternBytes, TaskAcceptor<FileSearchBean> resultCollector, int bufferSize) {
        this.resultCollector = resultCollector;
        this.patternBytes = patternBytes;
        this.bufferSize = bufferSize;

        this.kmpNext = new int[patternBytes.length];

        // Pre-compute
        int j = -1;
        for (int i = 0; i < patternBytes.length; i++) {
            if (i == 0) {
                kmpNext[i] = -1;
            } else if (patternBytes[i] != patternBytes[j]) {
                kmpNext[i] = j;
            } else {
                kmpNext[i] = kmpNext[j];
            }

            while (j >= 0 && patternBytes[i] != patternBytes[j]) {
                j = kmpNext[j];
            }

            j++;
        }
    }

    
    public void execute(FileSearchBean task) throws Exception {
        final FileInputStream fileInputStream = new FileInputStream(task.getInputFile());
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, bufferSize);

        try {
            int j = 0;
            int buff;
            while ((buff = bufferedInputStream.read()) != -1 && j < patternBytes.length) {
                while (j >= 0 && buff != (patternBytes[j] & 0xff)) {
                    j = kmpNext[j];
                }
                j++;

                if (j >= patternBytes.length) {
                    resultCollector.push(task);
                    break;
                    //  j = kmpNext[j];
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
