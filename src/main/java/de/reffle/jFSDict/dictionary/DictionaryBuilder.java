package de.reffle.jFSDict.dictionary;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.reffle.jFSDict.transTable.RichTransTableBuilder;
import de.reffle.jFSDict.transTable.TempState;
import de.reffle.jFSDict.util.Stats;
import de.reffle.jFSDict.util.Stopwatch;

public abstract class DictionaryBuilder {
  private static final char ANNOTATION_DELIMITER = '#';

  private final static Logger LOG = Logger.getLogger( DictionaryBuilder.class.getName() );

  protected Dictionary dict;
  protected RichTransTableBuilder ttBuilder;
  protected ArrayList<TempState> tempStates;
  private String lastWord;
  private int lastAnnotation;

  private Stopwatch addWordStopwatch = new Stopwatch();
  private Stats     addWordStats     = new Stats();

  public DictionaryBuilder() {
    tempStates = new ArrayList<TempState>();
  }


  abstract int findOrStoreTempState(int tempStateIndex);


  public Dictionary buildFromWordlist(File lexFile) throws FileNotFoundException,
      IOException {
        return buildFromWordlist(new FileReader(lexFile));
      }


  public Dictionary buildFromWordlist(InputStream inputStream) throws FileNotFoundException,
      IOException {
        return buildFromWordlist(new InputStreamReader(inputStream));
      }


  public Dictionary buildFromWordlist(Reader reader) throws FileNotFoundException,
  IOException {
    Stopwatch stopwatch = new Stopwatch();
    String line = null;
    BufferedReader bufReader = new BufferedReader(reader);
    while((line = bufReader.readLine()) != null) {
      DictEntry entry = parseEntry(line);
      addWord(entry);
    }
    bufReader.close();
    Dictionary finishedDict = finishAndGet();
    LOG.log(Level.INFO, "BuildFromWordlist: Took {0}ms.",
        new Object[]{stopwatch.getMillis()});
    return finishedDict;
  }


  public static DictEntry parseEntry(String line) {
    DictEntry entry = new DictEntry();
    int delimiterPos = line.indexOf((int)ANNOTATION_DELIMITER);
    if(delimiterPos == -1) {
      entry.setKey(line);
      entry.setValue(0);
    }
    else {
      entry.setKey(line.substring(0, delimiterPos));
      entry.setValue(Integer.parseInt(line.substring(delimiterPos+1)));
    }
    return entry;
  }


  public void addWord(String w) {
    addWord(w, 0);
  }


  private void addWord(DictEntry entry) {
    addWord(entry.getKey(), entry.getValue());
  }

  public void addWord(String w, int annotation) {
    LOG.log(Level.FINEST, "Add word {0} with annotation {1}", new Object[]{w, annotation});
    dict.incrementNrOfKeys();
    if(noWordsSeenYet()) {
      lastWord = new String(w);
      lastAnnotation = annotation;
      return;
    }
    int commonPrefix = findCommonPrefixLength(lastWord, w);

    adjustNumberOfTempStates();
    tempStates.get(lastWord.length()).setFinal(true);
    tempStates.get(lastWord.length()).setAnnotation(lastAnnotation);
    for(int i = lastWord.length(); i > commonPrefix; --i) {
      moveTempStateToTransTable(i);
    }
    lastWord       = w;
    lastAnnotation = annotation;

    if(dict.getNrOfKeys() % 1000 == 0) {
      if(dict.getNrOfKeys() % 100000 == 0) {
        LOG.log(Level.INFO, "{0} words inserted. {1} ms for last 100k.", new Object[]{dict.getNrOfKeys(), addWordStopwatch.getMillis()});
        addWordStopwatch.reset();
      }
      else if(dict.getNrOfKeys() % 1000 == 0) {
        LOG.log(Level.FINE, "{0} words inserted.", new Object[]{dict.getNrOfKeys()});
      }

    }
  }

  private static int findCommonPrefixLength(String s1, String s2) {
    int commonPrefix = 0;
    while(commonPrefix < s1.length() && commonPrefix < s2.length() &&
        (s1.charAt( commonPrefix ) == s2.charAt( commonPrefix))) {
      ++commonPrefix;
    }
    return commonPrefix;
  }

  public Dictionary finishAndGet() {
    finishConstruction();
    dict.trimEmptyTail();
    LOG.log(Level.INFO, "Built dictionary with {0} entries, {1} states, {2} slots.",
            new Object[]{dict.getNrOfKeys(), dict.getNrOfStates(), dict.getTableSize()});
    return dict;
  }


  private void finishConstruction() {
    LOG.log(Level.FINEST, "Enter finishConstruction.");

    adjustNumberOfTempStates();
    tempStates.get(lastWord.length()).setFinal(true);
    tempStates.get(lastWord.length()).setAnnotation(lastAnnotation);
    for(int i = lastWord.length(); i > 0; --i) {
      moveTempStateToTransTable(i);
    }
    int root = ttBuilder.storeTempState(tempStates.get(0));
    ttBuilder.setRoot(root);
  }


  private void moveTempStateToTransTable(int tempStateIndex) {
    int slot = findOrStoreTempState(tempStateIndex);
    tempStates.get(tempStateIndex - 1).addTransition(lastWord.charAt(tempStateIndex-1), slot, tempStates.get(tempStateIndex).getWordsFromHere());
    tempStates.get(tempStateIndex).reset();
    LOG.log(Level.FINER, "Add transition [{0}]--{1}--> {2}", new Object[]{(tempStateIndex-1),lastWord.charAt(tempStateIndex-1),slot});
  }


  private boolean noWordsSeenYet() {
    return lastWord == null;
  }

  private void adjustNumberOfTempStates() {
    while(tempStates.size() < lastWord.length() + 1) {
      tempStates.add(new TempState());
    }
  }

}

