package de.reffle.jfsdict.runtime;

import java.io.*;
import java.util.Calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.reffle.jfsdict.dictionary.*;
import de.reffle.jfsdict.transtable.RichTransTable.ValueType;
import de.reffle.jfsdict.transtable.WordIterator;
import de.reffle.jfsdict.util.Stopwatch;


public class DictRuntimeTest {

  private static Logger LOG = LoggerFactory.getLogger(DictRuntimeTest.class);

  public DictRuntimeTest() throws IOException {
  }


  @Test
  public void testTrie() throws Exception {
    testDurations(new TrieBuilder(), 1000, 1000, 100);
  }

  @Test
  public void testMinDic() throws Exception {
    testDurations(new MinDicBuilder(), 2000, 1000, 100);
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

    LOG.info("Start testDurations");
    Dictionary trie = null;

    {
      Stopwatch stopwatch = new Stopwatch();
      trie =	 createDict(aDictionaryBuilder);
      long duration = stopwatch.getMillis();
      LOG.info("Built trie for {} words in english_modern.lex: {} ms.", trie.getNrOfKeys(), duration );
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
      LOG.info("Traversed trie for english_modern.lex: {} ms.", duration);
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
    LOG.info("Lookup for all words of english_modern.lex: {} ms.", duration);
    assertTrue(String.format("Lookup (%d ms) should not exceed %d ms!", duration, aLookupMillies), (duration < aLookupMillies));
    assertTrue(String.format("Is something wrong? Lookup (%d) is much faster than expected (%d)!", duration, aLookupMillies), (duration > aLookupMillies/5));
  }
}
