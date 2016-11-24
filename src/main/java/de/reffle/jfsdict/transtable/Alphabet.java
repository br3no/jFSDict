package de.reffle.jfsdict.transtable;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alphabet implements Iterable<Character> {

  private static Logger LOG = LoggerFactory.getLogger(Alphabet.class);

  private Set<Character> charSet;

  public Alphabet() {
    charSet = new TreeSet<Character>();
  }

  public boolean hasChar( char c ) {
    return true;
  }

  public boolean addChar(char c) {
    if(charSet.add( c )) {
      LOG.trace("Added {} to alphabet. Has now {} entries.", c, charSet.size());
      return true;
    }
    return false;
  }

  public Iterator<Character> iterator() {
    return charSet.iterator();
  }
}
