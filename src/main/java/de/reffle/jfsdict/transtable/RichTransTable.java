package de.reffle.jfsdict.transtable;

import java.io.Serializable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.reffle.jfsdict.dictionary.DictEntry;

/**
 * TransTable implements an efficient way of storing and accessing transition tables for deterministic FSAs.
 *
 * This class only implements the actual data structure. Use TransTableBuilder to create and populate new TransTables.
 */
public class RichTransTable implements Iterable<DictEntry>, Serializable {

  private static Logger LOG = LoggerFactory.getLogger(RichTransTable.class);

  public static final int FAILSTATE_ID = 0;


  protected static final int LABEL_FOR_STATE_SLOTS = -1;
  protected static final int LABEL_FOR_FINAL_STATE_SLOTS = -2;

  private static final long serialVersionUID = 1L;

  /**
   * The array of labels - the first component of the compressed array holding the fsa.
   */
  private int label[];

  /**
   * The array of transition targets - the second component of the compressed array holding the fsa.
   */
  private int target[];


  private int addInfo[];


  protected int sparseTableSize;

  private int highestUsedSlot = 1;

  private int rootStateSlot;

  private Alphabet alphabet;

  private int nrOfStates    = 0;


  class PerfectHashAccumulator implements Cloneable {

    private int perfectHashValue;

    public PerfectHashAccumulator() {
      this(0);
    }

    public PerfectHashAccumulator(int aPerfectHashValue) {
      perfectHashValue = aPerfectHashValue;
    }

    protected void incrementBy(int x) {
      perfectHashValue += x;
    }

    public int getValue() {
      return perfectHashValue;
    }

    @Override
    protected PerfectHashAccumulator clone() {
      return new PerfectHashAccumulator(perfectHashValue);
    }
  }



  public RichTransTable() {
    sparseTableSize = 10000;
    alphabet = new Alphabet();
    label    = new int[sparseTableSize];
    target   = new int[sparseTableSize];
    addInfo  = new int[sparseTableSize];
  }


  public RichState getRoot() {
    return new RichState(this, getRootId());
  }


  public int getRootId() {
    return rootStateSlot;
  }


  public int delta(int aStateId, char c) {
    PerfectHashAccumulator dummy = new PerfectHashAccumulator();
    return delta(aStateId, c, dummy);
  }

  /**
   * Queries one transition in the TransTable. If the slot at
   * index state is a state slot and the transition with the given
   * label exists, the target state slot is returned. Otherwise, FAILSTATE_ID is returned.
   */
  public int delta(int aStateId, char c, PerfectHashAccumulator perfectHashValue) {
    if(isOutOfBounds(aStateId + c)) {
      return 0;
    }
    else if(label[aStateId + c] == c) {
      perfectHashValue.incrementBy(addInfo[aStateId + c]);
      return target[aStateId + c];
    }
    else {
      return FAILSTATE_ID;
    }
  }

  public int delta(int aStateId, String str) {
    PerfectHashAccumulator dummy = new PerfectHashAccumulator();
    return delta(aStateId, str, dummy);
  }

  public int delta(int aStateId, String str, PerfectHashAccumulator perfectHashValue) {
    for(int i = 0; i < str.length(); ++i) {
      aStateId = delta(aStateId, str.charAt(i), perfectHashValue);
      if(aStateId == FAILSTATE_ID) return aStateId;
    }
    return aStateId;
  }

  public boolean isFinal( int slot ) {
    return (isStateSlot(slot) && label[slot] == LABEL_FOR_FINAL_STATE_SLOTS);
  }

  public int getAnnotationAt(int slot) {
    return target[slot];
  }

  public int addInfoAt(int slot) {
    return addInfo[slot];
  }


  public int size() {
    return sparseTableSize;
  }

  public boolean hasWord(String w) {
    int arrivalState = delta(getRootId(), w);
    return isFinal(arrivalState);
  }

  public int lookup(String aStr) {
    return lookup(aStr, ValueType.ANNOTATION);
  }

  public int lookup(String aStr, ValueType aValueType) {
    RichState st = this.getRoot();
    st.walk(aStr);
    if(aValueType == ValueType.ANNOTATION) {

    }
    return 0;
  }


  public Integer getAnnotationAfter(String w) {
    int slot = delta(getRootId(), w);
    if(slot == FAILSTATE_ID || ! isFinal(slot)) {
      return null;
    }
    else {
      return getAnnotationAt(slot);
    }
  }

  public enum ValueType {ANNOTATION, INDEX}



  @Override
  public Iterator<DictEntry> iterator() {
    return iterator(ValueType.ANNOTATION); // TODO ??
  }


  public Iterator<DictEntry> iterator(ValueType aValueType) {
    return new WordIterator(this, aValueType);
  }

  public int getTableSize() {
    return sparseTableSize;
  }

  public void printWordList() {
    int count = 0;
    for(DictEntry w : this) {
      System.out.println( w.getKey() );
      if( ++count % 100000 == 0 ) LOG.trace("Printed {}k words.", count/1000 );
    }
  }


  /**
   * Prints the compressed table to STDOUT. For debugging.
   */
  public void printTable() {
    System.out.println( "##### TABLE ####" );
    for( int i = 0; i < sparseTableSize ; ++i ) {
      System.out.println( "(" + i + ")" + label[i] + "\t" + target[i] );
    }
  }


  public String toDot() {
    StringBuilder dotCode = new StringBuilder();
    dotCode.append("Digraph transtable {  // DOTCODE\n" );
    dotCode.append( "rankdir = \"LR\";  // DOTCODE\n" );
    for( int i = 0; i < sparseTableSize ; ++i ) {
      if(isStateSlot(i)) {
        dotCode.append( i + "[label=\"" + i + "(" + getAnnotationAt(i) + ")\"");
        if(isFinal(i)) dotCode.append(", peripheries=2");
        dotCode.append("]  // DOTCODE\n");
      }
      else if( ! isEmptySlot( i ) ) {
        dotCode.append( i - labelAt( i ) + "->" + targetAt(i) + "[label=\"" + (char) labelAt( i ) + "(" + addInfoAt(i) + ")\"]  // DOTCODE\n" );
      }
    }
    dotCode.append( "}  // DOTCODE\n" );

    return dotCode.toString();
  }

  protected boolean resize( int newSize ) {
    LOG.debug("Resize transTable from {} to {}", sparseTableSize, newSize);

    label   = resizeArray(label  , newSize);
    target  = resizeArray(target , newSize);
    addInfo = resizeArray(addInfo, newSize);
    sparseTableSize = newSize;
    return true;
  }

  public void trimEmptyTail() {
    resize(highestUsedSlot + 1);
  }

  private int[] resizeArray(int[] array, int newSize) {
    int currentSize = array.length;
    int newArray[] = new int[newSize];
    for(int i = 0; i < currentSize && i < newSize; ++i) {
      newArray[i] = array[i];
    }
    return newArray;

  }

  protected int labelAt(int i) {
    return label[i];
  }

  protected int targetAt(int i) {
    return target[i];
  }

  protected boolean isEmptySlot(int i) {
    return labelAt(i) == 0 && targetAt(i) == 0;
  }

  protected void set(int aSlot, int aLabel, int aTarget, int aAddInfo) {
    if( aLabel> 0 ) {
      alphabet.addChar((char) aLabel);
    }
    label[aSlot]   = aLabel;
    target[aSlot]  = aTarget;
    addInfo[aSlot] = aAddInfo;
    highestUsedSlot = Math.max(highestUsedSlot, aSlot);
  }

  protected void setRoot(int slot) {
    rootStateSlot = slot;
  }

  boolean isStateSlot( int slot ) {
    return ( label[slot] < 0 );
  }

  protected void setFinal( int slot ) throws Exception {
    if( ! isStateSlot(slot) ) throw new Exception( "jFSDict.TransTable.setFinal: no state slot." );
    target[slot] = 1;
  }

  protected boolean isOutOfBounds(int index) {
    return (index >= sparseTableSize);
  }

  protected Alphabet getAlphabet() {
    return alphabet;
  }


  public int getNrOfStates() {
    return nrOfStates;
  }


  void setNrOfStates(int nrOfStates) {
    this.nrOfStates = nrOfStates;
  }



} // end of class TransTable

