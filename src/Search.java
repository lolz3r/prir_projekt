

import watki.TaskAcceptor;
import watki.TaskExecutor;
import watki.algorytmy.*;
import staty.SimpleTaskAcceptorStats;
import extra.ExecutorThread;
import main.*; //wszystkie pakiety z maina

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Główna klasa wykonująca wyszukiwanie.
 * Działa następująco:
 * Na początku tworzona jest pula wątków które mają utworzone (lub pojedynczy wątek), a dla każdego wątku tworzona
 * kolejka plików i rozpoczynane jest ich przetwarzanie. 
 */

public class Search {
	public static StringBuilder s1 = new StringBuilder();
	public static List<String> s2 = new ArrayList<>();
    
	//główna funkcje programu szukająca, do wywołania z gui
    public static void szukaj(String fraza, String folder, int algo, int watki, int bufferSize, String kodowanie1){
    		Charset kodowanie = Charset.forName(kodowanie1); //kodowanie z argumentu
        
            final File glownyfolder = new File(folder);

            if (!glownyfolder.exists()) {
                try {
					throw new FileNotFoundException(glownyfolder + " nie istnieje!");
				} catch (FileNotFoundException e) {
					System.err.println("Folder "+ glownyfolder + " nie istnieje!");
				}
            }

            if (!glownyfolder.isDirectory()) {
                throw new IllegalArgumentException("Wybrany katalog musi być folderem a nie plikiem.");
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
                    final ExecutorThread<FileSearchBean> t = new ExecutorThread<FileSearchBean>(taskRunner, "Wątek #" + i);
                    t.start();

                    threadPool.add(t);
                }
            } else {
                // wersja jednowątkowa
                taskAcceptor = new SingleTaskQueue<FileSearchBean>(taskExecutor);
            }

            final SimpleTaskAcceptorStats<FileSearchBean> taskCounter = new SimpleTaskAcceptorStats<FileSearchBean>(taskAcceptor);

            final FileListing fileListing = new FileListing(glownyfolder, taskCounter);
            fileListing.run();

            // oczekiwanie na wątki
            for (ExecutorThread<FileSearchBean> t : threadPool) {
                try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            // czeka na pozostałe wątki
            final long przetworzonepliki = taskCounter.getTaskCount();
            final long czasw = System.currentTimeMillis() - startTime;
            long czaswatku = 0;

                for (ExecutorThread<FileSearchBean> t : threadPool) {
                    TaskRunner tr = t.getTaskRunner();
                    System.out.printf("Statystyki wątku '%s': przetworzone zadania: %d  czas: %d ms\n",
                            t.getName(), tr.getTasksProcessed(), tr.getThreadUptime());
                    
                    s1.append(String.format("Statystyki wątku '%s': przetworzone zadania: %d  czas: %d ms\n",
                            t.getName(), tr.getTasksProcessed(), tr.getThreadUptime()));
                    czaswatku += tr.getThreadUptime();
                }
                final long filesPerSecond = czasw > 0 ? (int)(przetworzonepliki*1000/czasw) : przetworzonepliki;
                System.out.printf("Czas wykonania: %d ms (czas wątku: %d ms), przetworzono plików: %d\n" +
                        "Prędkość wyszukiwania: %d plików/s\n", czasw, czaswatku, przetworzonepliki, filesPerSecond);
                s1.append(String.format("Czas wykonania: %d ms (czas wątku: %d ms), przetworzono plików: %d\n" +
                        "Prędkość wyszukiwania: %d plików/s\n", czasw, czaswatku, przetworzonepliki, filesPerSecond));
       
    }

    /**
     * pomoc
     */
    public static void pomoc() {
        System.out.println("java Wyszukiwarka [opcje] [--] <ścieżka> <wyszukiwana fraza>");
        System.out.println("    Lista opcji:");
        System.out.println("        -t <n>  \tUstawia ilość wątków na n (domyślnie tyle wątków ile rdzenii w systemie)");
        System.out.println("        -b <n>  \tUstawia wielkość bufora odczytu na <n> (domyślnie: 8192)");
        System.out.println("        -c <kodowanie>  \tUstawia kodowanie znaków na <kodowanie> (domyślnie: \"UTF-8\")");
        System.out.println("        -n      \tUżywa naiwnego algorytmu zamiast KMP (domyślnie: KMP)");
    System.out.println();
        System.out.println("    <ścieżka> - folder ktory jest przeszukiwany");
        System.out.println("    <wyszukiwana fraza> - fraza która jest poszukiwana we wszystkich plikach w wybranym folderze");
    }
}