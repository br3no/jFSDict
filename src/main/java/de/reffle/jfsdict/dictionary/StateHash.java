package de.reffle.jfsdict.dictionary;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.reffle.jfsdict.transtable.*;
import de.reffle.jfsdict.util.Stats;
import de.reffle.jfsdict.util.Stopwatch;

public class StateHash {
  private final static Logger LOG = Logger.getLogger(MinDicBuilder.class.getName());

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
      LOG.log(Level.FINE, "Collision stats: {0}", collisionStats.toString());
      collisionStats.reset();
    }
  }

  private void checkResize() {
    int filledRatio = hashdata.getFilledRatio();
    if(filledRatio > 40) {
      LOG.log(Level.FINE, "Hashtable is filled up to {0}%). Going to resize.", filledRatio);
      resize((int)(hashdata.size()  * 1.5));
    }
  }

  private void resize(int aNewSize) {
    Stopwatch stopwatch = new Stopwatch();
    LOG.log(Level.FINE, "Resizing hashtable to {0}", aNewSize);
    HashData oldHashData = hashdata;

    hashdata = new HashData(aNewSize);

    RichState state = new RichState(transTable, 0);
    for(int i = 0; i < oldHashData.size(); ++i) {
      state.set(oldHashData.at(i), -1);
      doPut(state, state.getStateId());
    }
    collisionStats.reset();
    LOG.log(Level.FINE, "Resized hashstable to {0} in {1} ms", new Object[] {hashdata.size(), stopwatch.getMillis()});
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
