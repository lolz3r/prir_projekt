
package main;

import watki.Task;

import java.io.File;

/**
 * Klasa reprezentująca wyszukiwany plik
 */
public class FileSearchBean implements Task {
    private final File inputFile;

    public FileSearchBean(File inputFile) {
        this.inputFile = inputFile;
    }

        public File getInputFile() {
        return inputFile;
    }

    
    public String toString() {
        return "FileSearchBean: " + inputFile.toString();
    }
}
