package de.reffle.jfsdict.levenshtein;

import java.util.logging.Logger;


public class LevFSA {

  private static final Logger LOG = Logger.getLogger(LevFSA.class.getName());

  private static AutData[] autDatas = {null, new AutData(1), new AutData(2), new AutData(3)};

  private String      pattern      = null;
  private int         levDistance  = 0;
  private CharVectors charVectors  = new CharVectors();
  private AutData autData = null;

  public LevFSA() {
  }

  public void loadPattern(String aPattern, int aLevDistance) {
    pattern     = aPattern;
    levDistance = aLevDistance;
    charVectors.loadPattern(aPattern);
    autData = autDatas[aLevDistance];
  }

  public LevState getRoot() {
    return new LevState(0, 0);
  }

  public boolean isFinal(LevState aLevState) {
    return getDistance(aLevState) > -1;
  }

  public LevState delta(LevState aLevState, char aChar) {
    int charVec = autData.getVectorSnippet(charVectors.get(aChar), pattern.length(), aLevState.getPatternPos(), levDistance);
//    System.out.println("charvec is " + Integer.toBinaryString(charVec));
    return new LevState(autData.delta(aLevState.getStateIndex(), charVec), aLevState.getPatternPos() + 1);
  }

  public LevState delta(LevState aLevState, String aStr) {
    for(int i = 0; i < aStr.length() && aLevState.isValid(); ++i) {
      aLevState = delta(aLevState, aStr.charAt(i));
    }
    return aLevState;
  }

  int getDistance(LevState aLevState) {
    if( ! aLevState.isValid()) {
      return -1;
    }
    return autData.getFinalInfo(aLevState.getStateIndex(), aLevState.getPatternPos(), pattern.length());
  }



}
