package de.reffle.jfsdict.dictionary;


public class HashData {
  private final static int HASHTABLE_DEFAULT_INIT_SIZE = (int)Math.scalb(1, 24)-1;


  private int[] hashtable;

  private int bucketsFilled = 0;


  public HashData() {
    this(HASHTABLE_DEFAULT_INIT_SIZE);
  }

  public HashData(int aSize) {
    hashtable = new int[aSize];
  }

  public int at(int aBucket) {
    return hashtable[aBucket];
  }

  public void set(int aBucket, int aStateId) {
    if(hashtable[aBucket] != 0) throw new RuntimeException("Tried to set an occupied bucket.");
    hashtable[aBucket] = aStateId;
    ++bucketsFilled;
  }

  public int getBucketsFilled() {
    return bucketsFilled;
  }

  public int getFilledRatio() {
    return (bucketsFilled * 100) / size();
  }

  public int size() {
    return hashtable.length;
  }

}
