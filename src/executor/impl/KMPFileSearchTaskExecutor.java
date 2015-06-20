
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
    private final byte[] frazab;
    private final int[] kmpNext;
    private final int bufferSize;
    private final TaskAcceptor<FileSearchBean> resultCollector;

    public KMPFileSearchTaskExecutor(byte[] frazab, TaskAcceptor<FileSearchBean> resultCollector) {
        this(frazab, resultCollector, DEFAULT_BUFFER_SIZE);
    }

    public KMPFileSearchTaskExecutor(byte[] frazab, TaskAcceptor<FileSearchBean> resultCollector, int bufferSize) {
        this.resultCollector = resultCollector;
        this.frazab = frazab;
        this.bufferSize = bufferSize;

        this.kmpNext = new int[frazab.length];

        // Pre-compute
        int j = -1;
        for (int i = 0; i < frazab.length; i++) {
            if (i == 0) {
                kmpNext[i] = -1;
            } else if (frazab[i] != frazab[j]) {
                kmpNext[i] = j;
            } else {
                kmpNext[i] = kmpNext[j];
            }

            while (j >= 0 && frazab[i] != frazab[j]) {
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
            while ((buff = bufferedInputStream.read()) != -1 && j < frazab.length) {
                while (j >= 0 && buff != (frazab[j] & 0xff)) {
                    j = kmpNext[j];
                }
                j++;

                if (j >= frazab.length) {
                	//ZNALEZIONO!
                    resultCollector.push(task);
                    System.out.println("znaleziono: " + bufferedInputStream.toString() + " " + j);
                    //break;
                    
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
