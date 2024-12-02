package com.fanyamin.bjava.demo;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileFilter;

public class FileIoDemo {
    private static final Logger logger = Logger.getLogger(FileIoDemo.class.getName());

    public static void main(String[] args) throws Exception {
        File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isHidden();
            }
        });

        File[] hiddenFiles2 = new File(".").listFiles(File::isHidden);
        //print files
        if (hiddenFiles != null) {
            Stream.of(hiddenFiles).forEach(f -> logger.info(f.getName()));
        }
        if (hiddenFiles2 != null) {
            Stream.of(hiddenFiles2).forEach(f -> logger.info(f.getName()));
        }
    }
}
