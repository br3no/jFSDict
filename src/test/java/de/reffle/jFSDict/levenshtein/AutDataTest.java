package de.reffle.jFSDict.levenshtein;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AutDataTest {
  @Test
  public void testVectorSnippets() throws Exception {
    AutData autData = new AutData1();

    assertEquals(0b011, autData.getVectorSnippet(0b1110000, 7, 0, 1));
    assertEquals(0b111, autData.getVectorSnippet(0b1110000, 7, 1, 1));
    assertEquals(0b110, autData.getVectorSnippet(0b1110000, 7, 2, 1));
    assertEquals(0b100, autData.getVectorSnippet(0b1110000, 7, 3, 1));
    assertEquals(0b000, autData.getVectorSnippet(0b1110000, 7, 4, 1));
  }
}
