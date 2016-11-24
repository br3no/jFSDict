package de.reffle.jfsdict.dictionary;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.reffle.jfsdict.transtable.*;
import de.reffle.jfsdict.util.Stats;
import de.reffle.jfsdict.util.Stopwatch;

public class StateHash {

  private static Logger LOG = LoggerFactory.getLogger(StateHash.class);

  private HashData hashdata = new HashData();

  private RichTransTable transTable;

  private Stats collisionStats = new Stats();

  public StateHash(RichTransTable aTransTable) {
    transTable = aTransTable;
  }

  public int get(TempState aTempState) {
    int bucket = getBucket(aTempState);
    int probes = 0;
    while(hashdata.at(bucket) != 0 && ! tempStateEqualsSlot(aTempState, hashdata.at(bucket))) {
      bucket = probeNext(bucket, probes);
      ++probes;
    }
    return hashdata.at(bucket);
  }

  public void put(AbstractState aState, int aSlot) {
    doPut(aState, aSlot);
    checkResize();
  }


  private void doPut(AbstractState aState, int aSlot) {
    int bucket = getBucket(aState);
    int probes = 0;
    while(hashdata.at(bucket) != 0) {
      bucket = probeNext(bucket, probes);
      ++probes;
    }
    hashdata.set(bucket, aSlot);
    collisionStats.put(probes);
    if(hashdata.getBucketsFilled() % 10000 == 0) {
      LOG.trace("Collision stats: {}", collisionStats.toString());
      collisionStats.reset();
    }
  }

  private void checkResize() {
    int filledRatio = hashdata.getFilledRatio();
    if(filledRatio > 40) {
      LOG.trace("Hashtable is filled up to {}%). Going to resize.", filledRatio);
      resize((int)(hashdata.size()  * 1.5));
    }
  }

  private void resize(int aNewSize) {
    Stopwatch stopwatch = new Stopwatch();
    LOG.trace("Resizing hashtable to {}", aNewSize);
    HashData oldHashData = hashdata;

    hashdata = new HashData(aNewSize);

    RichState state = new RichState(transTable, 0);
    for(int i = 0; i < oldHashData.size(); ++i) {
      state.set(oldHashData.at(i), -1);
      doPut(state, state.getStateId());
    }
    collisionStats.reset();
    LOG.trace("Resized hashstable to {} in {} ms", hashdata.size(), stopwatch.getMillis());
  }

  protected int getBucket(AbstractState state) {
    return Math.abs(state.hashCode()) % hashdata.size();
  }

  private boolean tempStateEqualsSlot(TempState tempState, int aTransTableSlot) {
    RichState state = new RichState(transTable, aTransTableSlot);
    return tempState.equals(state);
  }

  private int probeNext(int bucket, int probes) {
    return (bucket + (int)Math.pow(probes, 2)) % hashdata.size();
  }

}
