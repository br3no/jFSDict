package de.reffle.jfsdict.dictionary;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.reffle.jfsdict.transtable.RichTransTableBuilder;
import de.reffle.jfsdict.transtable.TempState;
import de.reffle.jfsdict.util.Stats;
import de.reffle.jfsdict.util.Stopwatch;

public class MinDicBuilder extends DictionaryBuilder {

  private static Logger LOG = LoggerFactory.getLogger(MinDicBuilder.class);

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
      LOG.trace("Could not find state with hash {}", tempState.hashCode());
      slot = ttBuilder.storeTempState(tempState);
      stateHash.put(tempState, slot);
      LOG.trace("Stored Tempstate {} to TransTable slot {}", tempStateIndex, slot);

      if(ttBuilder.getTransTable().getNrOfStates() % 10000 == 0) {
        LOG.debug("findOrStore Stats (nanoseconds): {}", statsFindOrStore);
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
