package de.reffle.jFSDict.util.test;

import java.util.Random;
import java.util.function.Function;

public class RandomErrors {

  private final Random random;

  public RandomErrors() {
    this(new Random());
  }

  public RandomErrors(Random aRandom) {
    random = aRandom;
  }

  public String getRandomString() {
    int length = random.nextInt(17) + 3;
    StringBuilder str = new StringBuilder();
    for(int i = 0;i<length;++i) {
      str.append(randomChar());
    }
    return str.toString();
  }

  public String addErrors(String aStr, int aNrOfErrors) {
    for(int i = 0; i < aNrOfErrors; ++i) {
      if(aStr.length() == 0) break;
      aStr = getRandomOperation().apply(aStr);
    }
    return aStr;
  }

  public String addRandomOperation(String aStr) {
    return getRandomOperation().apply(aStr);
  }

  public String addSubstitution(String aStr) {
    return replace(aStr, random.nextInt(aStr.length()), 1, randomChar());
  }

  public String addDeletion(String aStr) {
    return replace(aStr, random.nextInt(aStr.length()), 1, "");
  }

  public String addInsertion(String aStr) {
    return replace(aStr, random.nextInt(aStr.length()), 0, randomChar());
  }

  public String addTransposition(String aStr) {
    int pos = random.nextInt(aStr.length()-1);
    String transposed = Character.toString(aStr.charAt(pos+1)) + aStr.charAt(pos);
    return replace(aStr, pos, 2, transposed);
  }


  private Function<String, String> getRandomOperation() {
    float randomFloat = random.nextFloat();
    if(randomFloat < 0.25) {
      return a->addSubstitution(a);
    }
    else if(randomFloat < 0.5) {
      return a->addDeletion(a);
    }
    else if(randomFloat < 0.75) {
      return a->addDeletion(a);
    }
    else {
      return a->addTransposition(a);
    }
  }

  private String replace(String aStr, int aOffset, int aLength, char aReplacementChar) {
    return replace(aStr, aOffset, aLength, Character.toString(aReplacementChar));
  }

  private String replace(String aStr, int aOffset, int aLength, String aReplacementString) {
    StringBuilder newString = new StringBuilder();
    newString.append(aStr.substring(0, aOffset));
    newString.append(aReplacementString);
    newString.append(aStr.substring(aOffset + aLength));

    return newString.toString();
  }

  private char randomChar() {
    return (char)(random.nextInt(26) + 97);
  }
}
