package de.reffle.jfsdict.transtable;

import java.util.Iterator;
import java.util.Stack;

import de.reffle.jfsdict.dictionary.DictEntry;

/*
 * Even if code comments are discouraged in this project, they are unfortunately
 * necessary in this class, still ...
 */

public class FastWordIterator implements Iterator<DictEntry> {

  private RichTransTable myTransTable;
  private Stack<FastStackItem> stack;

  private StringBuilder prefix;

  private int curAnnotation;


  public FastWordIterator(RichTransTable aTransTable) {
    myTransTable = aTransTable;
    stack = new Stack<FastStackItem>();
    prefix = new StringBuilder(20);
    addStateToStack( myTransTable.getRootId() );
    if( ! myTransTable.isFinal( topState())) {
      next();
    }
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
        addStateToStack( myTransTable.delta( topState(), c ) );
        prefix.append( c );
        ///////// BREAK IF FINAL STATE //////////
        if( myTransTable.isFinal(topState())) {
          curAnnotation = myTransTable.getAnnotationAt(topState());
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


  private int topState() {
    return stack.peek().getState();
  }


  private void addStateToStack( int stateId ) {
    stack.add( new FastStackItem( stateId,
        RichState.labelIterator(myTransTable, stateId ) ) );
  }
}


class FastStackItem {
  private int                 stateId;
  private Iterator<Character> labelIt;


  FastStackItem(int st, Iterator<Character> it) {
    stateId = st;
    labelIt = it;
  }


  public Iterator<Character> getLabelIt() {
    return labelIt;
  }


  public int getState() {
    return stateId;
  }
}
