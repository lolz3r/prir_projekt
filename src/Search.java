

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
import java.util.LinkedList;

/**
 * G��wna klasa wykonuj�ca wyszukiwanie.
 * Dzia�a nast�puj�co:
 * Na pocz�tku tworzona jest pula w�tk�w kt�re maj� utworzone (lub pojedynczy w�tek), a dla ka�dego w�tku tworzona
 * kolejka plik�w i rozpoczynane jest ich przetwarzanie. 
 */
public class Search {
	public static StringBuilder s1 = new StringBuilder(); //string na wyniki
    
    //g��wna funkcje programu szukaj�ca, do wywo�ania z gui
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

            //do zwracania wynik�w
            final TaskAcceptor<FileSearchBean> reporter = new TaskAcceptor<FileSearchBean>() {
                public void push(FileSearchBean task) {
                	//pokazywanie wynik�w wyszukiwania:
                    System.out.println(task.getInputFile().toString());
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
            	//domy�lnie algorytm KMP
                taskExecutor = new KMPFileSearchTaskExecutor(frazabytes, reporter, bufferSize);
            }

            // wykonanie wielow�tkowe
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
                // wyszukiwanie w 1 w�tku
                taskAcceptor = new SingleTaskQueue<FileSearchBean>(taskExecutor);
            }

            final SimpleTaskAcceptorStats<FileSearchBean> taskCounter = new SimpleTaskAcceptorStats<FileSearchBean>(taskAcceptor);

            final FileListing fileListing = new FileListing(rootDirectory, taskCounter);
            fileListing.run();

            // czekaj na wszyskie w�tki
            for (ExecutorThread<FileSearchBean> t : threadPool) {
                try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            // oczekiwanie na w�tki
            final long totalTaskProcessed = taskCounter.getTaskCount();
            final long timeSpend = System.currentTimeMillis() - startTime;
            long threadTimeTotal = 0;

            if (1==1) {
                for (ExecutorThread<FileSearchBean> t : threadPool) {
                    TaskRunner tr = t.getTaskRunner();
                    System.out.printf("Statystyki w�tku '%s': przetworzone zadania: %d  czas: %d ms\n",
                            t.getName(), tr.getTasksProcessed(), tr.getThreadUptime());
                    s1.append(String.format("Statystyki w�tku '%s': przetworzone zadania: %d  czas: %d ms\n",
                            t.getName(), tr.getTasksProcessed(), tr.getThreadUptime() )); //do statystyk
                    threadTimeTotal += tr.getThreadUptime();
                }
                final long filesPerSecond = timeSpend > 0 ? (int)(totalTaskProcessed*1000/timeSpend) : totalTaskProcessed;
                System.out.printf("Czas wykonania: %d ms (czas w�tku: %d ms), przetworzono plik�w: %d\n" +
                        "Pr�dko�� wyszukiwania: %d plik�w/s\n", timeSpend, threadTimeTotal, totalTaskProcessed, filesPerSecond);
                s1.append(String.format("Czas wykonania: %d ms (czas w�tku: %d ms), przetworzono plik�w: %d\n" +
                        "Pr�dko�� wyszukiwania: %d plik�w/s\n", timeSpend, threadTimeTotal, totalTaskProcessed, filesPerSecond));
            }
       
    }

    /**
     * Just print help to standard output.
     */
    public static void printHelp() {
        System.out.println("java FileSearcher [options] [--] <path> <string pattern>");
        System.out.println("    Options:");
        System.out.println("        -t <n>  \tSet processing threads count to <n> (Default: 5)");
        System.out.println("        -b <n>  \tSet file-input buffer to <n> (Default: 8192)");
        System.out.println("        -c <charset>  \tSet character set to <charset> (Default: \"US-ASCII\")");
        System.out.println("        -s      \tPrint stats after processing (Default: no)");
        System.out.println("        -w      \tWait for user input before start (Default: no)");
        System.out.println("        -n      \tUse Naive search algorithm (Default: no)");
        System.out.println();
        System.out.println("    <path> - root path");
        System.out.println("    <string pattern> - string pattern for search");
    }
}
