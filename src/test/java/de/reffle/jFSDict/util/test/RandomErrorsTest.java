package de.reffle.jFSDict.util.test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import de.reffle.jFSDict.util.test.RandomErrors;

public class RandomErrorsTest {
  RandomErrors randomErrors = new RandomErrors(new Random(42));

  @Test
  public void test() throws Exception {

    String original = "original";

    checkVariants(
        original,
        a->randomErrors.addSubstitution(a),
        "[origihal, origimal, orrginal, orqginal, origipal, originaa, orgginal, oreginal, oriiinal, originzl]"
        );

    checkVariants(
        original,
        a->randomErrors.addDeletion(a),
        "[origina, oiginal, oriinal, oriinal, origial, origina, oriinal, originl, oiginal, oiginal]"
        );

    checkVariants(
        original,
        a->randomErrors.addInsertion(a),
        "[origkinal, ohriginal, originval, olriginal, origkinal, originfal, origdinal, origcinal, originval, xoriginal]"
        );


    checkVariants(
        original,
        a->randomErrors.addTransposition(a),
        "[origianl, roiginal, orgiinal, oirginal, orgiinal, roiginal, roiginal, roiginal, oirginal, oriignal]"
        );

    checkVariants(
        original,
        a->randomErrors.addErrors(a, 3),
        "[orgiln, oinial, oriil, oiigna, rpignial, oignla, vrinal, orial, gorinal, rinal]"
        );

  }

  private void checkVariants(String original, Function<String, String> aStringModifier, String aExpected) {
    assertEquals(aExpected, Arrays.toString(getVariants(original, 10, aStringModifier)));
  }

  private String[] getVariants(String aString, int aNumberOfVariants, Function<String, String> aStringModifier) {
    String[] variants = new String[aNumberOfVariants];
    for(int i = 0; i < aNumberOfVariants; ++i) {
      variants[i] = aStringModifier.apply(aString);
    }
    return variants;
  }

}
