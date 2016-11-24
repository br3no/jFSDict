package de.reffle.jfsdict.levenshtein;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharVectors {
  private static int KEEP_IN_ARRAY = 1000;

  @SuppressWarnings("unused")
  private static Logger LOG = LoggerFactory.getLogger(CharVectors.class);

  private String                  pattern;
  private List<Integer>           charvecList = new ArrayList<>();
  private Map<Character, Integer> charvecMap  = new HashMap<>();

  private static Integer zero = new Integer(0);

  public CharVectors() {
  }

  public void loadPattern(String aPattern) {
    reset();
    pattern = aPattern;
    for(int i=0;i<KEEP_IN_ARRAY;++i) charvecList.add(0);
    computeCharvecs();
  }

  /*
   * For each character c at position i in the pattern, set the i-th bit of the charVector for c.
   */
  void computeCharvecs() {
    int movingBit = 1 << (pattern.length()-1);
    for(int i=0; i < pattern.length(); ++i) {
      char c = pattern.charAt(i);
      set(c, get(c) | movingBit );
      movingBit = movingBit  >> 1;
    }
  }

  public void reset() {
    if(pattern != null) {
      for(int i=0; i < pattern.length(); ++i) {
        set(pattern.charAt(i), 0);
      }
    }
  }

  private void set(char c, int aCharvec) {
    if(c < KEEP_IN_ARRAY) {
      charvecList.set(c, aCharvec);
    }
    else {
      charvecMap.put(c, aCharvec);
    }
  }

  public int get(char c) {
    if(c < KEEP_IN_ARRAY) {
      return charvecList.get(c);
    }
    else {
      Integer vector = charvecMap.get(c);
      return (vector == null) ? zero : vector;
    }
  }

}
