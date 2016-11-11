package de.reffle.jFSDict.levenshtein;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharVectorsTest {
  @Test
  public void test() throws Exception {
    CharVectors charVectors = new CharVectors();
    charVectors.loadPattern("abcad");
    assertEquals(0b10010, charVectors.get('a'));
    assertEquals(0b01000, charVectors.get('b'));
    assertEquals(0b00100, charVectors.get('c'));
    assertEquals(0b00001, charVectors.get('d'));

  }
}
