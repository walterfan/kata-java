package com.fanyamin.bjava;
import org.apache.commons.cli.*;

public class App {


    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option question = new Option("q", "question", true, "question file path");
        question.setRequired(true);
        options.addOption(question);

        Option parameters = new Option("p", "parameters", true, "parameters file");
        parameters.setRequired(true);
        options.addOption(parameters);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String question_value = cmd.getOptionValue("question");
        String parameters_value = cmd.getOptionValue("parameters");

        System.out.println(question_value);
        System.out.println(parameters_value);

    }

}
