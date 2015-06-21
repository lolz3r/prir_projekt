

import executor.TaskAcceptor;
import executor.TaskExecutor;
import executor.impl.*;
import stats.SimpleTaskAcceptorStats;
import util.ExecutorThread;
import main.*; //wszystkie pakiety z maina

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Main class.
 * How it works:
 * <ul>
 *  <li> On start pool of threads will be created (or single-threaded queue if 0 threads specified). Each
 *  thread pulling queue for new file and begin processing.
 *  <li> {@link FileListing} performs recursive file listing and pushes file to the queue.
 *</ul>
 *
 */
public class Search {
	public static StringBuilder s1 = new StringBuilder();
	public static List<String> s2 = new ArrayList<>();
    
	//główna funkcje programu szukająca, do wywołania z gui
    public static void szukaj(String fraza, String folder, int algo, int watki, int bufferSize, String kodowanie1){
    		Charset kodowanie = Charset.forName(kodowanie1); //kodowanie z argumentu
        
            final File rootDirectory = new File(folder);

            if (!rootDirectory.exists()) {
                try {
					throw new FileNotFoundException(rootDirectory + " not found");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.err.println("Folder "+ rootDirectory + " nie istnieje!");
				}
            }

            if (!rootDirectory.isDirectory()) {
                throw new IllegalArgumentException("Input should be directory");
            }

            //do zwracania wyników
            final TaskAcceptor<FileSearchBean> reporter = new TaskAcceptor<FileSearchBean>() {
                public void push(FileSearchBean task) {
                	//pokazywanie wyników wyszukiwania:
                    System.out.println(task.getInputFile().toString());
                    s2.add(task.getInputFile().toString());
                }

                public void signalEndOfData() {
                }
            };

            final long startTime = System.currentTimeMillis();

			final byte[] frazabytes = fraza.getBytes(kodowanie); //wyszukiwana fraza na bajty

            // Algorithm selection
            TaskExecutor<FileSearchBean> taskExecutor;
            if (algo==2) {
                taskExecutor = new NaiveFileSearchTaskExecutor(frazabytes, reporter, bufferSize);
            } else if (algo==1) {
                taskExecutor = new KMPFileSearchTaskExecutorNIO(frazabytes, reporter, bufferSize);
            } else {
            	//domyślnie algorytm KMP
                taskExecutor = new KMPFileSearchTaskExecutor(frazabytes, reporter, bufferSize);
            }

            // wykonanie wielowątkowe
            TaskAcceptor<FileSearchBean> taskAcceptor;
            final LinkedList<ExecutorThread<FileSearchBean>> threadPool = new LinkedList<ExecutorThread<FileSearchBean>>();
            if (watki > 0) {
                final BlockingTaskQueue<FileSearchBean> taskQueue = new BlockingTaskQueue<FileSearchBean>(4096);
                taskAcceptor = taskQueue;

                for (int i = 0; i < watki; i++) {
                    final TaskRunner<FileSearchBean> taskRunner = new TaskRunner<FileSearchBean>(taskQueue, taskExecutor);
                    final ExecutorThread<FileSearchBean> t = new ExecutorThread<FileSearchBean>(taskRunner, "Executor #" + i);
                    t.start();

                    threadPool.add(t);
                }
            } else {
                // single-thread approach
                taskAcceptor = new SingleTaskQueue<FileSearchBean>(taskExecutor);
            }

            final SimpleTaskAcceptorStats<FileSearchBean> taskCounter = new SimpleTaskAcceptorStats<FileSearchBean>(taskAcceptor);

            final FileListing fileListing = new FileListing(rootDirectory, taskCounter);
            fileListing.run();

            // Wait for all threads...
            for (ExecutorThread<FileSearchBean> t : threadPool) {
                try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            // Wait for threads...
            final long totalTaskProcessed = taskCounter.getTaskCount();
            final long timeSpend = System.currentTimeMillis() - startTime;
            long threadTimeTotal = 0;

                for (ExecutorThread<FileSearchBean> t : threadPool) {
                    TaskRunner tr = t.getTaskRunner();
                    System.out.printf("Statystyki wątku '%s': przetworzone zadania: %d  czas: %d ms\n",
                            t.getName(), tr.getTasksProcessed(), tr.getThreadUptime());
                    
                    s1.append(String.format("Statystyki wątku '%s': przetworzone zadania: %d  czas: %d ms\n",
                            t.getName(), tr.getTasksProcessed(), tr.getThreadUptime()));
                    threadTimeTotal += tr.getThreadUptime();
                }
                final long filesPerSecond = timeSpend > 0 ? (int)(totalTaskProcessed*1000/timeSpend) : totalTaskProcessed;
                System.out.printf("Czas wykonania: %d ms (czas wątku: %d ms), przetworzono plików: %d\n" +
                        "Prędkość wyszukiwania: %d plików/s\n", timeSpend, threadTimeTotal, totalTaskProcessed, filesPerSecond);
                s1.append(String.format("Czas wykonania: %d ms (czas wątku: %d ms), przetworzono plików: %d\n" +
                        "Prędkość wyszukiwania: %d plików/s\n", timeSpend, threadTimeTotal, totalTaskProcessed, filesPerSecond));
       
    }

    /**
     * pomoc
     */
    public static void pomoc() {
        System.out.println("java Wyszukiwarka [opcje] [--] <�cie�ka> <wyszukiwana fraza>");
        System.out.println("    Lista opcji:");
        System.out.println("        -t <n>  \tUstawia ilo�� w�tk�w na n (domy�lnie tyle w�tk�w ile rdzenii w systemie)");
        System.out.println("        -b <n>  \tUstawia wielko�� bufora odczytu na <n> (domy�lnie: 8192)");
        System.out.println("        -c <kodowanie>  \tUstawia kodowanie znak�w na <kodowanie> (domy�lnie: \"UTF-8\")");
        System.out.println("        -n      \tU�ywa naiwnego algorytmu zamiast KMP (domy�lnie: KMP)");
    System.out.println();
        System.out.println("    <ścieżka> - folder ktory jest przeszukiwany");
        System.out.println("    <wyszukiwana fraza> - fraza kt�ra jest poszukiwana we wszystkich plikach w wybranym folderze");
}