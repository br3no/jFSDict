package de.reffle.jfsdict.dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.reffle.jfsdict.transtable.RichTransTableBuilder;

public class TrieBuilder extends DictionaryBuilder {

  private static Logger LOG = LoggerFactory.getLogger(TrieBuilder.class);

  public TrieBuilder() {
    super();
    dict = new Trie();
    ttBuilder = new RichTransTableBuilder(dict);
  }


  @Override
  int findOrStoreTempState(int tempStateIndex) {
    int slot = ttBuilder.storeTempState(tempStates.get(tempStateIndex));
    LOG.trace("Stored Tempstate {} to TransTable slot {}", tempStateIndex, slot);
    return slot;
  }


  @Override
  public Trie finishAndGet() {
    return (Trie)super.finishAndGet();
  }
}
