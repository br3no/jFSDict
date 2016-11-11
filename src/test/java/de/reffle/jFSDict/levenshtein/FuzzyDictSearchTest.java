package de.reffle.jFSDict.levenshtein;

import org.junit.Test;

import de.reffle.jFSDict.dictionary.Trie;
import de.reffle.jFSDict.dictionary.TrieBuilder;

public class FuzzyDictSearchTest {
  @Test
  public void test() throws Exception {
    Trie trie = getTrie();

    FuzzyDictSearch fuzzyDictSearch = new FuzzyDictSearch(trie);

    fuzzyDictSearch.query("anne", 1);
    fuzzyDictSearch.query("annx", 1);
    fuzzyDictSearch.query("bertram", 2);
  }

  public Trie getTrie() throws Exception {
    TrieBuilder trieBuilder = new TrieBuilder();
    trieBuilder.addWord("anna", 13);
    trieBuilder.addWord("anne", 14);
    trieBuilder.addWord("berta", 15);
    trieBuilder.addWord("bertram", 16);
    trieBuilder.addWord("otto", 17);
    return trieBuilder.finishAndGet();
  }

}
