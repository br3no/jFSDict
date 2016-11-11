package de.reffle.jFSDict.dictionary;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.reffle.jFSDict.dictionary.StateHash;
import de.reffle.jFSDict.transTable.*;

public class StateHashTest {

  @Test
  public void hashCodeAndEqualsTest() {
    RichTransTable transTable = new RichTransTable();
    RichTransTableBuilder transTableBuilder = new RichTransTableBuilder(transTable);

    StateHash stateHash = new StateHash(transTable);

    TempState oneTempState = getOneTempState();
    int oneStateId = transTableBuilder.storeTempState(oneTempState);
    RichState oneState = new RichState(transTable, oneStateId);
    stateHash.put(oneState, oneStateId);

    assertTrue(hashEqual(oneTempState, oneState));
    assertEquals(oneStateId, (int)stateHash.get(oneTempState));
    TempState equivalentTempState = getOneTempState();
    assertEquals(oneStateId, (int)stateHash.get(equivalentTempState));

    oneTempState.setFinal(true);
    assertFalse(hashEqual(oneTempState, oneState));
    assertEquals(0, (int)stateHash.get(oneTempState));

  }

  @Test
  public void testResize() throws Exception {

  }

  private boolean hashEqual(TempState aTempState, RichState aState) {
    return (aTempState.hashCode() == aState.hashCode() && aTempState.equals(aState));
  }


  private TempState getOneTempState() {
    TempState oneState = new TempState();
    oneState.addTransition('a', 42);
    oneState.addTransition('c', 43);
    oneState.addTransition('e', 44);
    return oneState;
  }

  private TempState getAnotherTempState() {
    TempState anotherState = new TempState();
    anotherState.addTransition('x', 100);
    return anotherState;
  }

}
