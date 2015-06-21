
package watki.algorytmy;

import main.FileSearchBean;
import watki.TaskAcceptor;
import watki.TaskExecutor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Zmodyfikowana implementacja algorytmu KMP
 * Złożoność obliczeniowa: O(m)+O(n)
 */

public class KMPFileSearchTaskExecutorNIO implements TaskExecutor<FileSearchBean> {
   // private final Logger logger = Logger.getLogger(KMPFileSearchTaskExecutorNIO.class);
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private final byte[] patternBytes;
    private final int bufferSize;
    private final Map<Long, ByteBuffer> byteBuffers = Collections.synchronizedMap(new HashMap<Long, ByteBuffer>());

    private final int[] kmpNext;
    private final TaskAcceptor<FileSearchBean> resultCollector;
    private int counter = 0;

    public KMPFileSearchTaskExecutorNIO(byte[] patternBytes, TaskAcceptor<FileSearchBean> resultCollector) {
        this(patternBytes, resultCollector, DEFAULT_BUFFER_SIZE);
    }

    public KMPFileSearchTaskExecutorNIO(byte[] patternBytes, TaskAcceptor<FileSearchBean> resultCollector, int bufferSize) {
        this.resultCollector = resultCollector;
        this.patternBytes = patternBytes;
        this.bufferSize = bufferSize;

        this.kmpNext = new int[patternBytes.length];
        // przeliczanie
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
        final FileChannel fc = fileInputStream.getChannel();

        // zostaw 1 bufor dla 1 wątku
        Long threadId = Thread.currentThread().getId();
        ByteBuffer byteBuffer = byteBuffers.get(threadId);
        if (byteBuffer == null) {
            byteBuffer = ByteBuffer.allocateDirect(bufferSize);
            byteBuffers.put(threadId, byteBuffer);
        }

        try {
            int j = 0;
            int buffReaded;
            byteBuffer.clear();

            while ((buffReaded = fc.read(byteBuffer)) != -1 && j < patternBytes.length) {
                if (buffReaded == 0)
                    continue;

                byteBuffer.position(0);
                byteBuffer.limit(buffReaded);

                while (byteBuffer.hasRemaining() && j < patternBytes.length) {
                    final int currChar = byteBuffer.get() & 0xff;

                    while (j >= 0 && currChar != (patternBytes[j] & 0xff)) {
                        j = kmpNext[j];
                    }
                    j++;

                    if (j >= patternBytes.length) {
                        resultCollector.push(task);
                        return;
                        
                    }
                }
                byteBuffer.clear();

            }
        } finally {
            try {
                fc.close();
            } catch(IOException ioe) {  }
            try {
                fileInputStream.close();
            } catch(IOException ioe) {  }
        }
    }
}
