package de.reffle.jFSDict.levenshtein;

import java.util.Iterator;

import de.reffle.jFSDict.dictionary.Dictionary;
import de.reffle.jFSDict.transTable.RichState;

public class FuzzyDictSearch {

  private Dictionary dict;

  LevFSA levFSA = new LevFSA();

  public FuzzyDictSearch(Dictionary aDict) {
    dict        = aDict;
  }

  public void query(String aPattern, int aMaxDistance) {
    levFSA.loadPattern(aPattern, aMaxDistance);
    RichState dicState = dict.getRoot();
    LevState levState  = levFSA.getRoot();

    System.out.println(String.format("---%s---", aPattern));
    query(dicState, levState, new StringBuilder());
  }

  private void query(RichState aDicState, LevState aLevState, StringBuilder aPrefix) {
    Iterator<Character> labels = aDicState.labelIterator();
    while(labels.hasNext()) {
      Character c = labels.next();
      RichState nextDicState = aDicState.delta(c);
      LevState  nextLevState = levFSA.delta(aLevState, c);

      if(nextDicState.isValid() && nextLevState.isValid()) {
        aPrefix.append(c);
        if(nextDicState.isFinal() && levFSA.isFinal(nextLevState)) {
          System.out.println(String.format("%s, %d, %d", aPrefix.toString(), levFSA.getDistance(nextLevState), nextDicState.getAnnotation()));
        }
        query(nextDicState, nextLevState, aPrefix);
        aPrefix.deleteCharAt(aPrefix.length()-1);
      }
    }
  }
}
