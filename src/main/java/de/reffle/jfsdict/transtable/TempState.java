/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.reffle.jfsdict.transtable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.reffle.jfsdict.cli.CompileDic;

public class TempState extends AbstractState {

  private static final Logger LOG = Logger.getLogger(CompileDic.class.getName());

  private List<Transition> transitions;

  private boolean isFinal   = false;

  private int annotation    = 0;

  private int wordsFromHere = 0;

  public TempState() {
    transitions = new ArrayList<Transition>();
  }


//  @Override
//  public Iterator<Character> labelIterator() {
//    return transitions.keySet().iterator();
//  }


  public void addTransition(char c, int target) {
    transitions.add(new Transition(c, target));
  }

  public void addTransition(char c, int target, int wordsFromTarget) {
    transitions.add(new Transition(c, target, wordsFromTarget));
    wordsFromHere += wordsFromTarget;
  }

  public List<Transition> getTransitions() {
    return transitions;
  }

  public boolean isFinal() {
    return isFinal;
  }

  public int getWordsFromHere() {
    return wordsFromHere + (isFinal() ? 1 : 0);
  }

  public void setFinal(boolean b) {
    isFinal = b;
  }

  public int getAnnotation() {
    return annotation;
  }


  public void setAnnotation(int aAnnotation) {
    annotation = aAnnotation;
  }


  public void reset() {
    transitions.clear();
    isFinal       = false;
    annotation    = 0;
    wordsFromHere = 0;
  }

  public int hashCode() {
    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    hashCodeBuilder.append(isFinal());
    hashCodeBuilder.append(getAnnotation());
    for(Transition transition : transitions) {
      hashCodeBuilder
        .append(transition.getLabel())
        .append(transition.getTarget());
    }
    return hashCodeBuilder.toHashCode();
  }



  @Override
  public boolean equals(Object other) {
    if( ! (other instanceof RichState)) {
      throw new RuntimeException("This works only with State");
    }
    RichState state = (RichState) other;
    if(this.isFinal() != state.isFinal()) return false;
    if(this.getAnnotation() != state.getAnnotation()) return false;
    Iterator<Character> stateLabelIterator = state.labelIterator();
    for(Transition transition : transitions) {
      if( ! stateLabelIterator.hasNext()) return false;
      Character stateLabel = stateLabelIterator.next();
      if(stateLabel != transition.getLabel()) return false;
      if(state.delta(stateLabel).getStateId() != transition.getTarget()) {
        return false;
      }
    }
    if(stateLabelIterator.hasNext()) return false;
    return true;
  }

  class Transition {
    private int label;
    private int target;
    private int wordsFromTarget;

    public Transition(int aLabel, int aTarget, int aPerfectHashValue) {
      label = aLabel;
      target = aTarget;
      wordsFromTarget = aPerfectHashValue;
    }

    public Transition(int aLabel, int aTarget) {
      label = aLabel;
      target = aTarget;
      wordsFromTarget = 0;
    }

    public int getLabel() {
      return label;
    }

    public int getTarget() {
      return target;
    }

    public int getWordsFromTarget() {
      return wordsFromTarget;
    }
  }
}
