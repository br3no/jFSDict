package de.reffle.jfsdict.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.*;

import de.reffle.jfsdict.dictionary.*;

public class CompileDic {

  public static void main(String[] args) {
    try {
      CompileDic compileMinDic = new CompileDic();
      compileMinDic.doIt(args);
    }
    catch(Exception e) {
      LOG.log(Level.SEVERE, "Error: "+ e.getMessage());
    }
  }


  private static final Logger LOG = Logger.getLogger(CompileDic.class.getName());
  private CommandLine commandLine;
  private DictionaryBuilder dicBuilder;
  private static Options options;


  private void doIt(String[] args) throws Exception {
    parseCommandLine(args);
    if(commandLine.hasOption("help")) {
      printHelp();
      return;
    }

    Dictionary minDic = buildDictionary();
    LOG.log(Level.INFO, "Built dictionary with {0} entries, {1} states.", new Object[]{minDic.getNrOfKeys(), minDic.getNrOfStates()});
    if(commandLine.hasOption("dot")) {
      System.out.println(minDic.toDot());
    }
  }


  private void prepareCommandLineOptions() {
    options = new Options();
    options.addOption("i", "input"   , true  , "The input file. Keys must be sorted. If not provided, read from stdin.");
    options.addOption("o", "output"  , true  , "The output file. Usually somename.dict.");
    options.addOption(     "trie"    , false , "Create a trie instead of a minimal automaton.");
    options.addOption(     "dot"     , false , "Create dot output instead of binary format.");
    options.addOption("h", "help"    , false , "Display help.");
  }


  private Dictionary buildDictionary() throws Exception {
    if(commandLine.hasOption("trie")) {
      LOG.log(Level.INFO, "Will build using TrieBuilder.");
      dicBuilder =  new TrieBuilder();
    }
    else {
      LOG.log(Level.INFO, "Will build using MinDicBuilder.");
      dicBuilder = new MinDicBuilder();
    }
    if(commandLine.hasOption("input")) {
      String inputFileName = commandLine.getOptionValue("input");
      return buildFromFile(dicBuilder, inputFileName);
    }
    else {
      return buildFromStdin(dicBuilder);
    }
  }


  private static Dictionary buildFromStdin(DictionaryBuilder dicBuilder) throws Exception {
    LOG.log(Level.INFO, "Will build dictfrom STDIN.");
    BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
    return dicBuilder.buildFromWordlist(reader);
  }


  private static Dictionary buildFromFile(DictionaryBuilder dicBuilder, String pathToFile) throws Exception {
    LOG.log(Level.INFO, "Will build dict from input file {0}", pathToFile);
    File file = new File(pathToFile);
    return dicBuilder.buildFromWordlist(file);
  }


  private void parseCommandLine(String[] args) throws Exception {
    prepareCommandLineOptions();

    CommandLineParser parser = new BasicParser();
    commandLine = parser.parse(options, args);
  }


  private void printHelp() {
    HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.printHelp("compileDic", options);
  }
}
