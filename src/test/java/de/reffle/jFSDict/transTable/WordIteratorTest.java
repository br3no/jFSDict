/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.reffle.jFSDict.transTable;

import java.util.Iterator;

import org.junit.Test;

import de.reffle.jFSDict.dictionary.DictEntry;
import de.reffle.jFSDict.transTable.RichTransTable;
import de.reffle.jFSDict.transTable.RichTransTableBuilder;
import de.reffle.jFSDict.transTable.TempState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WordIteratorTest {

  @Test
  public void testWordIteratorWithTransTable() {
    RichTransTableBuilder ttBuilder = new RichTransTableBuilder(new RichTransTable());
    testWordIterator(ttBuilder);
  }

  @Test
  public void testWordIteratorWithRichTransTable() {
    RichTransTableBuilder ttBuilder = new RichTransTableBuilder(new RichTransTable());
    testWordIterator(ttBuilder);
  }


  public void testWordIterator(RichTransTableBuilder aTTBuilder) {
    RichTransTable tt = getSimpleWordGraph(aTTBuilder);
    Iterator<DictEntry> wit = tt.iterator();
    DictEntry nextEntry;

    assertTrue(wit.hasNext());
    nextEntry = wit.next();
    assertEquals("anna", nextEntry.getKey());
    assertEquals(42, nextEntry.getValue());

    assertTrue(wit.hasNext());
    nextEntry = wit.next();
    assertEquals("anne", nextEntry.getKey());

    assertTrue(wit.hasNext());
    nextEntry = wit.next();
    assertEquals("babs", nextEntry.getKey());

    assertTrue(wit.hasNext());
    nextEntry = wit.next();
    assertEquals("babsi", nextEntry.getKey());

    assertFalse(wit.hasNext());
  }

    static RichTransTable getSimpleWordGraph(RichTransTableBuilder aTTBuilder) {
        TempState st = new TempState();
        int lastState = 0;
        //anna
        //anne
        //barbara
        //berta
        /*
         *  --  a (1) n (2) n (3) a ((4))
         *  |                  |- e  -|
         *  |
         * (0)- b (9) a (8) b (7) s ((6)) i ((5))
         *
         */

        //(4)
        st.setFinal(true);
        st.setAnnotation(42);
        lastState = aTTBuilder.storeTempState(st);

        //(3)
        st.reset();
        st.addTransition('a', lastState);
        st.addTransition('e', lastState);
        lastState = aTTBuilder.storeTempState(st);

        //(2)
        st.reset();
        st.addTransition('n', lastState);
        lastState = aTTBuilder.storeTempState(st);

        //(1)
        st.reset();
        st.addTransition('n', lastState);
        lastState = aTTBuilder.storeTempState(st);

        int aState = lastState;

        //(5)
        st.reset();
        st.setFinal(true);
        lastState = aTTBuilder.storeTempState(st);

        //(6)
        st.reset();
        st.addTransition('i', lastState);
        st.setFinal(true);
        lastState = aTTBuilder.storeTempState(st);

        //(7)
        st.reset();
        st.addTransition('s', lastState);
        lastState = aTTBuilder.storeTempState(st);

        //(8)
        st.reset();
        st.addTransition('b', lastState);
        lastState = aTTBuilder.storeTempState(st);

        //(8)
        st.reset();
        st.addTransition('a', lastState);
        lastState = aTTBuilder.storeTempState(st);

        //(0)
        st.reset();
        st.addTransition('a', aState);
        st.addTransition('b', lastState);
        lastState = aTTBuilder.storeTempState(st);
        aTTBuilder.setRoot(lastState);

        RichTransTable tt = aTTBuilder.getTransTable();
        assertTrue( tt.hasWord("anna"));
        assertTrue( tt.hasWord("anne"));
        assertTrue( tt.hasWord("babs"));
        assertTrue( tt.hasWord("babsi"));

        return tt;
    }
}
