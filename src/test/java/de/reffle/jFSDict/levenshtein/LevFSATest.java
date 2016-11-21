package de.reffle.jFSDict.levenshtein;

import java.util.Random;
import java.util.logging.Logger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.reffle.jFSDict.util.test.RandomErrors;

public class LevFSATest {
  private static final Logger LOG = Logger.getLogger(LevFSATest.class.getName());

  @Test
  public void walkTest() throws Exception {
    LevFSA levFSA = new LevFSA();
    levFSA.loadPattern("pattern", 1);

    LevState st = levFSA.getRoot();

    st = levFSA.delta(st, 'p');
    assertEquals("(0,1)", st.toString());

    st = levFSA.delta(st, 'a');
    assertEquals("(0,2)", st.toString());

    st = levFSA.delta(st, 't');
    assertEquals("(3,3)", st.toString());
    assertFalse(levFSA.isFinal(st));

    st = levFSA.delta(st, "ter");
    assertEquals("(0,6)", st.toString());
    assertTrue(levFSA.isFinal(st));
    assertEquals(1, levFSA.getDistance(st));

    st = levFSA.delta(st, 'n');
    assertEquals("(0,7)", st.toString());
    assertTrue(levFSA.isFinal(st));
    assertEquals(0, levFSA.getDistance(st));

    st = levFSA.delta(st, 'x');
    assertEquals("(1,8)", st.toString());
    assertTrue(levFSA.isFinal(st));
    assertEquals(1, levFSA.getDistance(st));

    st = levFSA.delta(st, 'x');
    assertEquals("(-1,9)", st.toString());
    assertFalse(levFSA.isFinal(st));

  }

  @Test
  public void test() throws Exception {
    checkMatch("pattern", "pattern"  , 1, 0);
    checkMatch("pattern", "puttern"  , 1, 1);
    checkMatch("pattern", "pattren"  , 1, 1);
    checkMatch("pattern", "xattern"  , 1, 1);
    checkMatch("pattern", "patterx"  , 1, 1);
    checkMatch("pattern", "patter"   , 1, 1);
    checkMatch("pattern", "patterna" , 1, 1);
    checkMatch("p"      , "p"        , 1, 0);
    checkMatch("p"      , ""         , 1, 1);

    checkMatch("pattern", "pattern"  , 2, 0);
    checkMatch("pattern", "paxtern"  , 2, 1);
    checkMatch("pattern", "paxern"   , 2, 2);
    checkMatch("pattern", "ttern"    , 2, 2);
    checkMatch("pattern", "patte"    , 2, 2);
    checkMatch("pattern", "paxte"    , 2, -1);
  }

  private void checkMatch(String aPattern, String aInput, int aMaxDistance, int aExpectedDistance) {
    LevFSA levFSA = new LevFSA();
    levFSA.loadPattern(aPattern, aMaxDistance);
    LevState st = levFSA.getRoot();
    st = levFSA.delta(st, aInput);
    if(aExpectedDistance > aMaxDistance) aExpectedDistance = -1;
    assertEquals(aExpectedDistance, levFSA.getDistance(st));
  }

  @Test
  public void randomTest() throws Exception {
    Random random = new Random(42);
    RandomErrors randomErrors = new RandomErrors(random);
    int nrOfRandomTests = 100000;

    for(int i = 0; i < nrOfRandomTests; ++i) {
      String str = randomErrors.getRandomString();
      String modfifiedString = randomErrors.addErrors(str, random.nextInt(3));
      int actualDistance = LevenshteinDistance.computeLevenshteinDistance(str, modfifiedString);
//      System.out.println(String.format("%s, %s, %d", str, modfifiedString, actualDistance));
      checkMatch(str, modfifiedString, 2, actualDistance);
    }
  }

}
