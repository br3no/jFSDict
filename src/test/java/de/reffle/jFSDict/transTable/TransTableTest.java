/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.reffle.jFSDict.transTable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.reffle.jFSDict.transTable.RichTransTable;

public class TransTableTest {
  /**
   * Create a very simple TransTable with random labels and targets
   */
  private RichTransTable getSimpleTransTable() {
    RichTransTable tt = new RichTransTable();
    assertEquals("Expected size after initialisation", 100, tt.size());
    tt.set(1, 3, 30, -1);
    tt.set(42, 4, 40, -1);
    tt.set(99, 5, 50, -1);
    return tt;
  }


  @Test
  public void testResize() {
    RichTransTable tt = getSimpleTransTable();

    assertEquals("Expected size after initialisation", 100, tt.size());

    tt.resize(200);
    assertEquals("Expected size after resize", 200, tt.size());
    assertEquals("Old values are preserved.", 3, tt.labelAt(1));
    assertEquals("Old values are preserved.", 4, tt.labelAt(42));
    assertEquals("Old values are preserved.", 5, tt.labelAt(99));
    assertEquals("Old values are preserved.", 30, tt.targetAt(1));
    assertEquals("Old values are preserved.", 40, tt.targetAt(42));
    assertEquals("Old values are preserved.", 50, tt.targetAt(99));

    tt.resize(150);
    assertEquals("Resize works for shrinking the table.", 150, tt.size());
  }


  @Test
  public void testLabelAt() {
    RichTransTable tt = getSimpleTransTable();
    assertEquals(4, tt.labelAt(42));
    try {
      tt.labelAt(103);
      fail("Should throw ArrayIndexOutOfBoundsException");
    }
    catch(ArrayIndexOutOfBoundsException exc) {
      // ok
    }
  }

  @Test
  public void testTargetAt() {
    System.out.println("targetAt");
    RichTransTable tt = getSimpleTransTable();
    assertEquals(40, tt.targetAt(42));
    try {
      tt.targetAt(103);
      fail("Should throw ArrayIndexOutOfBoundsException");
    }
    catch(ArrayIndexOutOfBoundsException exc) {
      // ok
    }
  }

}
