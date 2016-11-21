package de.reffle.jfsdict.dictionary;

import java.util.Scanner;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import de.reffle.jfsdict.dictionary.Dictionary;
import de.reffle.jfsdict.dictionary.MinDicBuilder;


public class MinDicBuilderTest {

  @Test
  public void test() throws Exception {
    MinDicBuilder minDicBuilder = new MinDicBuilder();

    minDicBuilder.addWord("");
    minDicBuilder.addWord("anna");
    minDicBuilder.addWord("anne");
    minDicBuilder.addWord("annemarie");
    minDicBuilder.addWord("johanna");
    minDicBuilder.addWord("johanne");
    minDicBuilder.addWord("susanne");
    Dictionary minDic = minDicBuilder.finishAndGet();

    assertEquals(
        getResourceAsString("/some_names.dot"),
        minDic.toDot());

  }

  public static String getResourceAsString(String aResource) { // TODO Move this to utils, somewhere
    Scanner scanner = new Scanner(MinDicBuilder.class.getResourceAsStream(aResource), "UTF-8");
    String string = scanner.useDelimiter("\\A").next();
    scanner.close();
    return string;
  }
}
