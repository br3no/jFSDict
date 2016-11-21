package de.reffle.jfsdict.levenshtein;

class Match {
  public String match;
  public int levDistance;
  public int annotation;

  public Match(String aMatch, int aLevDistance, int aAnnotation) {
    match = aMatch;
    levDistance = aLevDistance;
    annotation = aAnnotation;
  }

  public String toString() {
    return String.format("%s, %d, %d", match, levDistance, annotation);
  }

}