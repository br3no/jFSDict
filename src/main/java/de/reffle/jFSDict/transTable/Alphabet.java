package de.reffle.jFSDict.transTable;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.reffle.jFSDict.dictionary.MinDicBuilder;

public class Alphabet implements Iterable<Character> {

  private final static Logger LOG = Logger.getLogger(MinDicBuilder.class.getName());

  private Set<Character> charSet;

  public Alphabet() {
    charSet = new TreeSet<Character>();
  }

  public boolean hasChar( char c ) {
    return true;
  }

  public boolean addChar(char c) {
    if(charSet.add( c )) {
      LOG.log(Level.FINE, "Added {0} to alphabet. Has now {1} entries.", new Object[]{c, charSet.size()});
      return true;
    }
    return false;
  }

  public Iterator<Character> iterator() {
    return charSet.iterator();
  }
}
