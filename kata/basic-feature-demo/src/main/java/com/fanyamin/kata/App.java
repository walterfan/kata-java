package com.fanyamin.kata;
import org.apache.commons.cli.*;

public class App {


    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option kata = new Option("k", "kata", true, "kata name");
        kata.setRequired(true);
        options.addOption(kata);

        Option parameters = new Option("p", "para", true, "kata parameters");
        parameters.setRequired(true);
        options.addOption(parameters);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("kata name", options);
            
            System.exit(1);
        }

        String kata_value = cmd.getOptionValue("kata");
        String para_value = cmd.getOptionValue("para");

        System.out.println(kata_value);
        System.out.println(para_value);

    }

}
