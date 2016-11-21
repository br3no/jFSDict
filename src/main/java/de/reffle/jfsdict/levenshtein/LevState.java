package de.reffle.jfsdict.levenshtein;

public class LevState {

  private int stateIdx;
  private int patternPos;

  public LevState(int aStateIdx, int aPatternPos) {
    stateIdx = aStateIdx;
    patternPos = aPatternPos;
  }

  public boolean isValid() {
    return stateIdx != -1;
  }

  public int getStateIndex() {
    return stateIdx;
  }

  public int getPatternPos() {
    return patternPos;
  }

  @Override
  public String toString() {
    return String.format("(%s,%s)", getStateIndex(), getPatternPos());
  }
}
