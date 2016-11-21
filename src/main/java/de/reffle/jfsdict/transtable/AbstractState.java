package de.reffle.jfsdict.transtable;


public abstract class AbstractState {

  public abstract boolean isFinal();

  public abstract int hashCode();


//  @Override
//  public boolean equals(Object other) {
//    if( ! (other instanceof TempState)) {
//      throw new RuntimeException("This works only with TempState");
//    }
//    TempState tempState = (TempState) other;
//    return false;
//  }
}