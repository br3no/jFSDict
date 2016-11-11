package de.reffle.jFSDict.dictionary;

import de.reffle.jFSDict.transTable.RichTransTable;

/**
 * Dictionary is a sub-class of TransTable,
 * and a super-class for different dictionary implementations like Trie or MinDic,
 * holding common code for those.
 */
public class Dictionary extends RichTransTable {

  private static final long serialVersionUID = 1L;

  public Dictionary() {
    super();
    nrOfKeys = 0;
  }

  protected int nrOfKeys;

  public int getNrOfKeys() {
    return nrOfKeys;

  }

  protected void setNrOfKeys(int n) {
    nrOfKeys = n;
  }

  protected void incrementNrOfKeys() {
    ++nrOfKeys;
  }

}