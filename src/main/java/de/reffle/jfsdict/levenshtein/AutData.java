package de.reffle.jfsdict.levenshtein;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutData {

  private static final Logger LOG = Logger.getLogger(AutData.class.getName());

  public int distance;
  public int bitVectorLength;
  public int zeroShift  = bitVectorLength / 2;
  public int nrOfTransitionsPerState = 1 << bitVectorLength;
  public int[] transitions;
  public int[] finalInfo;
  public int[] suffixMatch;
  private int z2k1;

  public AutData(int aDistance) {
    distance                = aDistance;
    bitVectorLength         = 2*distance + 1;
    zeroShift               = bitVectorLength / 2;
    nrOfTransitionsPerState = 1 << bitVectorLength;
    z2k1                    = (1<<bitVectorLength)-1;

    getAutData();
  }

  public int delta(int aStateId, int aCharVec) {
    return transitions[aStateId * nrOfTransitionsPerState + aCharVec];
  }

  public int getFinalInfo(int aStateId, int aPatternPos, int aPatternLength) {
    int finalInfoOffset = aPatternLength - aPatternPos + zeroShift;
    if( finalInfoOffset > ( bitVectorLength - 1 ) ) {
        return -1;
    }
    else if( finalInfoOffset >= 0 ) {
        return finalInfo[aStateId * bitVectorLength + finalInfoOffset];
    }
    else {
        return -1;
    }
  }

  public int getZeroShift() {
    return zeroShift;
  }

  int getVectorSnippet(int aCharVec, int aPatternLength, int aPatternPos, int aDistance) {
    int r = 0;
    int shiftRight = (aPatternLength - aPatternPos - 1 - getZeroShift());
    if(shiftRight > 0) r = (aCharVec >> shiftRight);
    else               r = (aCharVec << -shiftRight);
    return (r & getZ2k1());
  }

  public int getZ2k1() {
    return z2k1 ;
  }

  private void getAutData() {
    try {
      String resourcePath = "/autdata/autdata" + distance;
      LOG.log(Level.INFO, "Load levenshtein automaton for distance {0} at {1}", new Object[]{distance, resourcePath});
      InputStream fileStream = this.getClass().getResourceAsStream(resourcePath);
      BufferedReader bufReader = new BufferedReader(new InputStreamReader(fileStream));

      transitions = splitToInt(bufReader.readLine());
      finalInfo   = splitToInt(bufReader.readLine());
      suffixMatch = splitToInt(bufReader.readLine());
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  private int[] splitToInt(String transitionLine) {
    String[] elementsString = transitionLine.split(",");
    int[]    elementsInt    = new int[elementsString.length];
    for(int i=0; i<elementsString.length; ++i) {
      elementsInt[i] = Integer.parseInt(elementsString[i]);
    }
    return elementsInt;
  }

}

