package de.reffle.jFSDict.transTable;

import java.util.Iterator;
import java.util.Stack;

import de.reffle.jFSDict.dictionary.DictEntry;
import de.reffle.jFSDict.transTable.RichTransTable.ValueType;

/*
 * Even if code comments are discouraged in this project, they are unfortunately
 * necessary in this class, still ...
 */

public class WordIterator implements Iterator<DictEntry> {

  private RichTransTable myTransTable;

  private ValueType valueType;

  private Stack<StackItem> stack;

  private StringBuilder prefix;

  private int curAnnotation;



  public WordIterator(RichTransTable aTransTable, ValueType aValueType) {
    myTransTable = aTransTable;
    valueType = aValueType;
    stack = new Stack<StackItem>();
    prefix = new StringBuilder(20);
    addStateToStack(myTransTable.getRoot());
    if(topState().isFinal()) {
      curAnnotation = getValue();
    }
    else {
      next();
    }
  }


  private int getValue() {
    if     (valueType == ValueType.ANNOTATION) return topState().getAnnotation();
    else if(valueType == ValueType.INDEX)      return topState().getIndex();
    else throw new IllegalArgumentException("Unknown value type.");
  }


  public boolean hasNext() {
    return ! stack.empty();
  }


  public DictEntry next() {
    // the current prefix is what we return afterwards
    DictEntry entryToReturn = new DictEntry();
    entryToReturn.setKey(prefix.toString());
    entryToReturn.setValue(curAnnotation);

    // now we move on to the next final state
    do {
      if( stack.peek().getLabelIt().hasNext() ) {
        char c = stack.peek().getLabelIt().next();
        addStateToStack(topState().delta(c));
        prefix.append(c);
        ///////// BREAK IF FINAL STATE //////////
        if(topState().isFinal()) {
          curAnnotation = getValue();
          break;
        }
      }
      else {
        stack.pop();

        if( ! stack.empty() ) { // in this case prefix is already empty
          prefix.setLength( prefix.length() - 1 );
        }
      }
    } while( ! stack.empty() );

    return entryToReturn;
  }


  public void remove() {
    throw new UnsupportedOperationException("Not supported.");
  }


  private RichState topState() {
    return stack.peek().getState();
  }


  private void addStateToStack(RichState aState) {
    stack.add(new StackItem(aState, aState.labelIterator()));
  }
}


class StackItem {
  private RichState           state;
  private Iterator<Character> labelIt;


  StackItem(RichState aState, Iterator<Character> it) {
    state = aState;
    labelIt = it;
  }


  public Iterator<Character> getLabelIt() {
    return labelIt;
  }


  public RichState getState() {
    return state;
  }
}
