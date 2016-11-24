package de.reffle.jfsdict.dictionary;

import java.io.*;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.reffle.jfsdict.transtable.RichTransTableBuilder;
import de.reffle.jfsdict.transtable.TempState;
import de.reffle.jfsdict.util.Stats;
import de.reffle.jfsdict.util.Stopwatch;

public abstract class DictionaryBuilder {
  private static final char ANNOTATION_DELIMITER = '#';

  private static Logger LOG = LoggerFactory.getLogger(DictionaryBuilder.class);

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
    LOG.info("BuildFromWordlist: Took {}ms.", stopwatch.getMillis());
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
    LOG.trace("Add word {} with annotation {}", w, annotation);
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
        LOG.info("{} words inserted. {} ms for last 100k.", dict.getNrOfKeys(), addWordStopwatch.getMillis());
        addWordStopwatch.reset();
      }
      else if(dict.getNrOfKeys() % 1000 == 0) {
        LOG.trace("{} words inserted.", dict.getNrOfKeys());
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
    LOG.info("Built dictionary with {} entries, {} states, {} slots.", dict.getNrOfKeys(), dict.getNrOfStates(), dict.getTableSize());
    return dict;
  }


  private void finishConstruction() {
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
    LOG.trace("Add transition [{}]--{}--> {}", (tempStateIndex-1),lastWord.charAt(tempStateIndex-1),slot);
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

