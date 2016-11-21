package de.reffle.jFSDict.levenshtein;

import java.util.ArrayList;
import java.util.List;

public class MatchReceiverList implements MatchReceiver {

  List<Match> matches = new ArrayList<>();

  @Override
  public void receive(Match aMatch) {
    matches.add(aMatch);
  }

  public void clear() {
    matches.clear();
  }

  public List<Match> getList() {
    return matches;
  }

  @Override
  public String toString() {
    return matches.toString();
  }
};
