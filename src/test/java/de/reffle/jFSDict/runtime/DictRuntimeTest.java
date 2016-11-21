package de.reffle.jFSDict.runtime;

import java.io.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.reffle.jFSDict.dictionary.*;
import de.reffle.jFSDict.transTable.RichTransTable.ValueType;
import de.reffle.jFSDict.transTable.WordIterator;
import de.reffle.jFSDict.util.Stopwatch;


public class DictRuntimeTest {

  private static final Logger LOG = Logger.getLogger(DictRuntimeTest.class.getName());


  public DictRuntimeTest() throws IOException {
  }


  @Test
  public void testTrie() throws Exception {
    testDurations(new TrieBuilder(), 1000, 1000, 100);
  }

  @Test
  public void testMinDic() throws Exception {
    testDurations(new MinDicBuilder(), 10000, 1000, 100);
  }


  public Dictionary createDict(DictionaryBuilder aDictionaryBuilder) throws IOException {
    InputStream testFileStream = getTestFileStream();

    return aDictionaryBuilder.buildFromWordlist(testFileStream);
  }


  private InputStream getTestFileStream() {
    InputStream testFileStream = this.getClass().getResourceAsStream("/english_modern.lex");
    return testFileStream;
  }


  @SuppressWarnings("unused")
  public void iterateTrie(Dictionary trie) {
    WordIterator wit = new WordIterator(trie, ValueType.ANNOTATION);
    DictEntry dummy;
    int countWords = 0;
    while (wit.hasNext()) {
      dummy = wit.next();
      ++countWords;
    }
    assertEquals(trie.getNrOfKeys(), countWords);
  }

  public void testDurations(DictionaryBuilder aDictionaryBuilder, int aConstructionMillies, int aTraversalMillies, int aLookupMillies) throws IOException {

    LOG.log(Level.INFO, "Start testDurations");
    Dictionary trie = null;

    {
      Stopwatch stopwatch = new Stopwatch();
      trie =	 createDict(aDictionaryBuilder);
      long duration = stopwatch.getMillis();
      LOG.log(Level.INFO,
          "Built trie for {0} words in english_modern.lex: {1} ms.",
          new Object[]{trie.getNrOfKeys(), duration });
      assertTrue(String.format("Construction of dict (&d ms) should not exceed %d ms!", duration, aConstructionMillies), (duration < aConstructionMillies));
      assertTrue(String.format("Is something wrong? Construction of dict (%d ms) is much faster than expected (%d ms)!", duration, aConstructionMillies), (duration > aConstructionMillies/5));
    }

    /////  Traversal
    {
      assertTrue( trie != null );
      long start = Calendar.getInstance().getTimeInMillis();

      iterateTrie(trie);

      long end = Calendar.getInstance().getTimeInMillis();
      long duration = end - start;
      Logger.getLogger(getClass().getName()).log(Level.INFO,
          "Traversed trie for english_modern.lex: {0} ms.",
          new Object[]{duration});
      assertTrue(String.format("Traversal (%d ms) should not exceed %d ms!", duration, aTraversalMillies), (duration < aTraversalMillies));
      assertTrue(String.format("Is something wrong? Traversal (%d) is much faster than expected (%d)!", duration, aTraversalMillies), (duration > aTraversalMillies/5));
    }

    // lookup
    BufferedReader br = new BufferedReader(new InputStreamReader(getTestFileStream()));
    String w;

    long start = Calendar.getInstance().getTimeInMillis();

    while((w = br.readLine()) != null) {
      assertTrue(trie.hasWord(w));
    }
    br.close();
    long end = Calendar.getInstance().getTimeInMillis();
    long duration = end - start;
    Logger.getLogger(getClass().getName()).log(Level.INFO,
        "Lookup for all words of english_modern.lex: {0} ms.",
        new Object[]{duration});
    assertTrue(String.format("Lookup (%d ms) should not exceed %d ms!", duration, aLookupMillies), (duration < aLookupMillies));
    assertTrue(String.format("Is something wrong? Lookup (%d) is much faster than expected (%d)!", duration, aLookupMillies), (duration > aLookupMillies/5));
  }
}
