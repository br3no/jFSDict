package de.reffle.jFSDict.transTable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import de.reffle.jFSDict.transTable.RichTransTable;
import de.reffle.jFSDict.transTable.RichTransTableBuilder;
import de.reffle.jFSDict.transTable.TempState;

public class TransTableBuilderTest {

    public TransTableBuilderTest() {
    }


    @Test
    public void testAddState() {
        System.out.println("addState");
        RichTransTable transTable = new RichTransTable();
        RichTransTableBuilder builder = new RichTransTableBuilder( transTable );
        TempState tempState = new TempState();
        tempState.addTransition( new Character( 'a' ), new Integer( 3 ) );
        tempState.addTransition( new Character( 'c' ), new Integer( 7 ) );
        tempState.setAnnotation(42);
        int expectedSlot = 1;
        int slot = builder.storeTempState(tempState);
        assertEquals(expectedSlot, slot);
        assertEquals(3, transTable.delta(slot, 'a'));
        assertEquals(7, transTable.delta(slot, 'c'));
        assertEquals(42, transTable.getAnnotationAt(slot));

        tempState = new TempState();
        tempState.addTransition( new Character( 'a' ), new Integer( 4 ) );
        tempState.addTransition( new Character( 'c' ), new Integer( 8 ) );
        tempState.setAnnotation(43);
        expectedSlot = 2;
        slot = builder.storeTempState(tempState);
        assertEquals(expectedSlot, slot);
        assertEquals(4, transTable.delta(slot, 'a'));
        assertEquals(8, transTable.delta(slot, 'c'));
        assertEquals(43, transTable.getAnnotationAt(slot));
    }

    @Test
    public void testToDot() {
      RichTransTable tt = new RichTransTable();
      RichTransTableBuilder builder = new RichTransTableBuilder( tt );
      TempState tempState = new TempState();
      tempState.addTransition( new Character( 'a' ), new Integer( 3 ) );
      tempState.addTransition( new Character( 'c' ), new Integer( 7 ) );
      // TODO
    }

}