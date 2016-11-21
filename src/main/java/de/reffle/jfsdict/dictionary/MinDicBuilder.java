package de.reffle.jfsdict.dictionary;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.reffle.jfsdict.transtable.RichTransTableBuilder;
import de.reffle.jfsdict.transtable.TempState;
import de.reffle.jfsdict.util.Stats;
import de.reffle.jfsdict.util.Stopwatch;

public class MinDicBuilder extends DictionaryBuilder {
  private final static Logger LOG = Logger.getLogger(MinDicBuilder.class.getName());

  private StateHash stateHash;

  private Stats statsFindOrStore = new Stats();


  private Stopwatch storeStopwatch = new Stopwatch();

  public MinDicBuilder() {
    dict = new Dictionary();
    tempStates = new ArrayList<TempState>();
    ttBuilder = new RichTransTableBuilder(dict);
    stateHash = new StateHash(dict);
  }


  @Override
  int findOrStoreTempState(int tempStateIndex) {
    storeStopwatch.reset();
    TempState tempState = tempStates.get(tempStateIndex);
    Integer slot = stateHash.get(tempState);

    if(slot == Dictionary.FAILSTATE_ID) {
      LOG.log(Level.FINER, "Could not find state with hash {0}", new Object[]{tempState.hashCode()});
      slot = ttBuilder.storeTempState(tempState);
      stateHash.put(tempState, slot);
      LOG.log(Level.FINER, "Stored Tempstate {0} to TransTable slot {1}", new Object[]{tempStateIndex, slot});

      if(ttBuilder.getTransTable().getNrOfStates() % 10000 == 0) {
        LOG.log(Level.FINE, "findOrStore Stats (nanoseconds): {0}", new Object[]{statsFindOrStore});
        statsFindOrStore.reset();
      }
    }
    statsFindOrStore.put(storeStopwatch.getNanos());
    return slot;
  }


  @Override
  public Dictionary finishAndGet() {
    return super.finishAndGet();
  }
}
