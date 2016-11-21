package de.reffle.jfsdict.transtable;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.reffle.jfsdict.transtable.TempState.Transition;


public class RichTransTableBuilder {
  private static final Logger LOG = Logger.getLogger(RichTransTableBuilder.class.getName());

  private static final int PROBING_WINDOW = 1000;

  protected RichTransTable transTable;

  private int firstCellToProbe = 1;



  public RichTransTableBuilder(RichTransTable tt) {
    transTable = tt;
  }

  public RichTransTable getTransTable() {
    return transTable;
  }

  private int findSlot(TempState tempState) {
    int slotForState = firstCellToProbe;
    while( ! slotFitsForState(slotForState, tempState)) {
      slotForState = findNextFreeSlot(slotForState + 1);
    }

    return slotForState;
  }


  private boolean slotFitsForState(int slotForState, TempState tempState) {
    if( ! transTable.isEmptySlot(slotForState)) return false;

    for(Transition transition : tempState.getTransitions()) {
      int slotForTransition = slotForState + transition.getLabel();
      enlargeTransTableIfNecessary(slotForTransition + 1);

      if( ! transTable.isEmptySlot(slotForTransition)) return false;
    }
    return true;
  }


  private int findNextFreeSlot(int slot) {
    while( ! transTable.isEmptySlot(slot)) {
      ++slot;
      enlargeTransTableIfNecessary(slot);
    }
    return slot;
  }

  public int storeTempState(TempState tempState) {
    int stateSlot = findSlot(tempState);
    writeStateSlot(stateSlot, tempState);
    storeTransitions(stateSlot, tempState);

    LOG.log(Level.FINEST, "Inserted state at slot  {0}",  stateSlot);
    transTable.setNrOfStates(transTable.getNrOfStates() + 1);

    firstCellToProbe = Math.max(findNextFreeSlot(firstCellToProbe), stateSlot - PROBING_WINDOW);

    return stateSlot;
  }


  protected void storeTransitions(int aStateSlot, TempState aTempState) {
    int perfectHashValue = aTempState.isFinal() ? 1 : 0;
    for(Transition transition : aTempState.getTransitions()) {
      storeOneTransition(aStateSlot, transition.getLabel(), transition.getTarget(), perfectHashValue);
      perfectHashValue += transition.getWordsFromTarget();
    }
  }


  protected void storeOneTransition(int aStateSlot, int aLabel, int aTarget, int aPerfectHashValue) {
    LOG.log( Level.FINEST, "Add transition, {0} --{1}--> {2}", new Object[]{aStateSlot, aLabel, aTarget});
    int slot = aStateSlot + aLabel;
    transTable.set(slot, aLabel, aTarget, aPerfectHashValue);
  }


  private void writeStateSlot(int slot, TempState tempState) {
    int label = tempState.isFinal() ?
                   RichTransTable.LABEL_FOR_FINAL_STATE_SLOTS : RichTransTable.LABEL_FOR_STATE_SLOTS;
    transTable.set(slot, label, tempState.getAnnotation(), -1); // add fast traversal info here as addInfo
  }


  private void enlargeTransTableIfNecessary(int necessaryTransTableSize) {
    while( transTable.size() < necessaryTransTableSize ) {
      transTable.resize( transTable.size() * 2 );
    }
  }


  public void setRoot( int stateId ) {
    transTable.setRoot( stateId );
  }
}
