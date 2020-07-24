package ch.so.agi.ili2c;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ili2c", mixinStandardHelpOptions = true,
    version = "ili2c 5.1.1",
    description = "Parses and compiles INTERLIS data model definitions. Other options include conversion from INTERLIS Version 1 and back (option -o1) and generation of an XML-Schema, released 2001 (option -oXSD).")
public class App implements Callable<Integer> {

    @Option(names = "--trace", description = "Display detailed trace messages.")
    boolean trace;
    
    @Option(names = "--quiet", description = "Suppress info messages.")
    boolean quiet;


    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Hallo Stefan.");
        return 0;
    }
}
