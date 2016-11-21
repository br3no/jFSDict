package de.reffle.jfsdict.transtable;

import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.reffle.jfsdict.cli.CompileDic;
import de.reffle.jfsdict.transtable.RichTransTable.PerfectHashAccumulator;

public class RichState extends AbstractState {

  private static final Logger LOG = Logger.getLogger(CompileDic.class.getName());

  final RichTransTable transTable;

  int stateId;

  PerfectHashAccumulator perfHashAccumulator;


  public RichState(RichTransTable aRichTransTable, int aStateId) {
    this(aRichTransTable, aStateId, 0);
  }

  private RichState(RichTransTable aRichTransTable, int aStateId, int aPerfectHashValue) {
    transTable = aRichTransTable;
    set(aStateId, aPerfectHashValue);
  }

  public void set(int aStateId, int aPerfectHashValue) {
    stateId = aStateId;
    perfHashAccumulator = transTable.new PerfectHashAccumulator(aPerfectHashValue);
  }

  @Override
  public RichState clone() {
    return new RichState(transTable, stateId, perfHashAccumulator.getValue());
  }

  public RichState delta(char c) {
    RichState targetState = this.clone();
    targetState.walk(c);
    return targetState;
  }

  public RichState delta(String aStr) {
    RichState targetState = this.clone();
    targetState.walk(aStr);
    return targetState;
  }

  public void walk(char c) {
    stateId = transTable.delta(stateId, c, perfHashAccumulator);
  }

  void walk(String aStr) {
    stateId = transTable.delta(stateId, aStr, perfHashAccumulator);
  }

  public boolean isFinal() {
    return transTable.isFinal(stateId);
  }

  public boolean isValid() {
    return (stateId != RichTransTable.FAILSTATE_ID);
  }

  public Iterator<Character> labelIterator() {
    return labelIterator(transTable, stateId);
  }


  protected static Iterator<Character> labelIterator(RichTransTable transTable, int stateId) {
    return new RichLabelIterator(transTable, stateId);
  }


  public int getAnnotation() {
    return transTable.getAnnotationAt(getStateId());
  }

  public int getIndex() {
    return perfHashAccumulator.getValue();
  }

  public int getStateId() {
    return stateId;
  }


  @Override
  public int hashCode() {
    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    hashCodeBuilder.append(isFinal());
    hashCodeBuilder.append(getAnnotation());
    for(Iterator<Character> labelIterator = labelIterator(); labelIterator.hasNext();) {
      Character c = labelIterator.next();
      hashCodeBuilder
        .append(c)
        .append(transTable.delta(stateId, c));
    }
    return hashCodeBuilder.toHashCode();
  }

  @Override
  public boolean equals(Object otherObj) {
    // TODO Lots of room for optimization here!
    if( ! (otherObj instanceof RichState)) {
      throw new RuntimeException("This works only with State");
    }
    RichState other = (RichState) otherObj;
    if(this.isFinal() != other.isFinal()) return false;
    if(this.getAnnotation() != other.getAnnotation()) return false;
    Iterator<Character> thisLabelIterator =  this.labelIterator();
    Iterator<Character> otherLabelIterator = other.labelIterator();

    while(thisLabelIterator.hasNext()) {
      if( ! otherLabelIterator.hasNext()) return false;
      Character label = thisLabelIterator.next();
      if(label != otherLabelIterator.next()) return false;
      if(this.delta(label).getStateId() != other.delta(label).getStateId()) {
        return false;
      }
    }
    if(otherLabelIterator.hasNext()) return false;
    return true;
  }
}

/**
 * This class is used to iterate over all characters where the given state in the
 * given TransTable owns an outgoing transition.
 */
class RichLabelIterator implements Iterator<Character> {

  private final RichTransTable myTransTable;
  private final int stateId;
  Character curChar;

  /*
   * The position of charIterator is always one to the right of the current character
   */
  Iterator<Character> charIterator;

  RichLabelIterator(RichTransTable transTable, int stId) {
    myTransTable = transTable;
    stateId = stId;
    charIterator = myTransTable.getAlphabet().iterator();
    curChar = null;
    next();

  }

  public boolean hasNext() {
    return curChar != null;
  }

  public final Character next() {
    Character returnChar = (curChar == null)? null : new Character(curChar);
    boolean foundNewOne = false;
    while(charIterator.hasNext()) {
      curChar = charIterator.next();
      if(myTransTable.delta(stateId, curChar) != 0) {
        foundNewOne = true;
        break;
      }
    }
    if(! foundNewOne) {
      curChar = null;
    }
    return returnChar;
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported.");
  }

}