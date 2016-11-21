package de.reffle.jfsdict.dictionary;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.reffle.jfsdict.dictionary.*;
import de.reffle.jfsdict.transtable.RichTransTable.ValueType;

public class DictionaryBuilderTest {
  private static final Logger LOG = Logger.getLogger(DictionaryBuilderTest.class.getName());
  Map<String, Integer> someSourceDict;

  public DictionaryBuilderTest() {
  }

  @Before
  public void setUp() throws Exception {
    someSourceDict = new TreeMap<String, Integer>();
    someSourceDict.put(""     , 41);
    someSourceDict.put("anna" , 42);
    someSourceDict.put("anne" , 43);
    someSourceDict.put("bert" , 44);
    someSourceDict.put("berta", 45);
    someSourceDict.put("bohne", 46);
    someSourceDict.put("cäsar", 47);
    someSourceDict.put("resal", 48);
    someSourceDict.put("uli"  , 49);

  }

  @Test
  public void insertAndIterateTrie() {
    DictionaryBuilder trieBuilder = new TrieBuilder();
    insertAndIterate(trieBuilder);
  }

  @Test
  public void insertAndIterateMinDic() {
    DictionaryBuilder minDicBuilder = new MinDicBuilder();
    insertAndIterate(minDicBuilder);
  }

  public void insertAndIterate(DictionaryBuilder aDictBuilder) {
    for(Entry<String, Integer> entry: someSourceDict.entrySet()) {
      aDictBuilder.addWord(entry.getKey(), entry.getValue());
    }
    Dictionary dict = aDictBuilder.finishAndGet();
    Iterator<Entry<String, Integer>> sourceIterator = someSourceDict.entrySet().iterator();
    Iterator<DictEntry> dictIteratorAnn   = dict.iterator(ValueType.ANNOTATION);
    Iterator<DictEntry> dictIteratorIndex = dict.iterator(ValueType.INDEX);

    int expectedIndex = 0;
    while(sourceIterator.hasNext() && dictIteratorAnn.hasNext() && dictIteratorIndex.hasNext()) {
      Entry<String, Integer> nextInSource = sourceIterator.next();
      DictEntry nextInDictAnn  = dictIteratorAnn.next();
      DictEntry nextInDictIndex= dictIteratorIndex.next();
      LOG.log(Level.FINE, "nextInSource is {0}, nextInDict is {1}", new Object[]{nextInSource, nextInDictAnn} );
      assertEquals(nextInSource.getKey(), nextInDictAnn.getKey());
      assertEquals(nextInSource.getValue().intValue(), nextInDictAnn.getValue());
      assertEquals(expectedIndex, nextInDictIndex.getValue());
      ++expectedIndex;
    }
    if(sourceIterator.hasNext()) {
      fail("sourceIterator should not hasNext()");
    }
    if(dictIteratorIndex.hasNext()) {
      fail("sourceIterator should not hasNext()");
    }

  }

  @Test
  public void testNonAscii() throws Exception {
    DictionaryBuilder dictionaryBuilder = new MinDicBuilder();
    dictionaryBuilder.addWord("cäsar");
    Dictionary dict = dictionaryBuilder.finishAndGet();
    assertEquals(6, dict.getNrOfStates());
  }

  @Test
  public void testParseEntry() throws Exception {
    DictEntry entry = DictionaryBuilder.parseEntry("hänsel#42");
    assertEquals(5, entry.getKey().length());
    assertEquals("(hänsel,42)", entry.toString());
  }
}
