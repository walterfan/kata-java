package com.fanyamin.kata.demo;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FileSearcher {

    public static void search(String folder, String keyword) throws IOException {
        try (Stream<Path> pathStream = Files.walk(Paths.get(folder), FileVisitOption.FOLLOW_LINKS)) {
            pathStream.filter(Files::isRegularFile) // find regular file
                    .filter(FileSystems.getDefault().getPathMatcher("glob:**/*.java")::matches)// find *.java
                    .flatMap(ThrowingFunction.unchecked(path -> Files.readAllLines(path).stream()
                            .filter(line -> Pattern.compile(keyword).matcher(line).find())
                            .map(line -> path.getFileName() + ": " + line)))
                    .forEach(System.out::println);

        }
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option kata = new Option("p", "path", true, "folder path");
        kata.setRequired(true);
        options.addOption(kata);

        Option parameters = new Option("k", "keyword", true, "keyword");
        parameters.setRequired(true);
        options.addOption(parameters);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;// not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("kata name", options);
        }
        String path = ".";
        String keyword = "public class";
        if (cmd != null) {
            path = cmd.getOptionValue("path", ".");
            keyword = cmd.getOptionValue("keyword", "public class");
        }

        try {
            search(path, keyword);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
