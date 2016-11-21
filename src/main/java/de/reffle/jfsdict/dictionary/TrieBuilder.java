package de.reffle.jfsdict.dictionary;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.reffle.jfsdict.transtable.RichTransTableBuilder;

public class TrieBuilder extends DictionaryBuilder {
  private final static Logger LOG = Logger.getLogger(TrieBuilder.class.getName());

  public TrieBuilder() {
    super();
    dict = new Trie();
    ttBuilder = new RichTransTableBuilder(dict);
  }


  @Override
  int findOrStoreTempState(int tempStateIndex) {
    int slot = ttBuilder.storeTempState(tempStates.get(tempStateIndex));
    LOG.log(Level.FINER, "Stored Tempstate {0} to TransTable slot {1}", new Object[]{tempStateIndex, slot});
    return slot;
  }


  @Override
  public Trie finishAndGet() {
    return (Trie)super.finishAndGet();
  }
}
