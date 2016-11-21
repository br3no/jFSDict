package de.reffle.jfsdict.transtable;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.reffle.jfsdict.transtable.*;

/**
 *
 * @author uli
 */
public class RichStateTest {

  RichTransTable aTransTable;
  RichTransTableBuilder ttbuilder;

  RichState     state_aToZ;
  int           stateId_aToZ;

  RichState     state_afz;
  int           stateId_afz;

  @Before
  public void setUp() {
    aTransTable = new RichTransTable();
    RichTransTableBuilder ttbuilder = new RichTransTableBuilder(aTransTable);
    TempState tempState = new TempState();

    for( char c = 'a'; c <= 'z'; ++c ) {
        tempState.addTransition( c, 42 );
    }
    stateId_aToZ = ttbuilder.storeTempState(tempState);
    state_aToZ = new RichState(aTransTable, stateId_aToZ);

    tempState = new TempState();
    tempState.addTransition('a', 42);
    tempState.addTransition('f', 43);
    tempState.addTransition('z', 44);
    stateId_afz = ttbuilder.storeTempState(tempState);
    state_afz = new RichState(aTransTable, stateId_afz);

  }

    @Test
    public void testDelta() {
      for( char c = 'a'; c <= 'z'; ++c ) {
        assertEquals(42, state_aToZ.delta(c).getStateId());
      }

      assertEquals(42, state_afz.delta('a').getStateId());
      assertEquals(43, state_afz.delta('f').getStateId());
      assertEquals(44, state_afz.delta('z').getStateId());
    }

    @Test
    public void testWalk() throws Exception {
      state_afz.walk('f');
      assertEquals(43, state_afz.getStateId());
    }

    @Test
    public void testLabelIterator() {
        Iterator<Character> it_aToZ = RichState.labelIterator(aTransTable, stateId_aToZ);
        for(char c = 'a'; c <= 'z'; ++c) {
            assertEquals(c, it_aToZ.next().charValue());
        }
        assertFalse(it_aToZ.hasNext());

        Iterator<Character> it_afz = RichState.labelIterator( aTransTable, stateId_afz);
        assertTrue(it_afz.hasNext());
        assertEquals('a', it_afz.next().charValue() );
        assertEquals('f', it_afz.next().charValue() );
        assertEquals('z', it_afz.next().charValue() );
        assertFalse( it_afz.hasNext() );
    }
}
