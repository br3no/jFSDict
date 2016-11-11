package de.reffle.jFSDict.util;

public class Stats {

  long  nrOfData;
  float sum;
  float min;
  float max;

  public Stats() {
    reset();
  }

  public void put(float aDataPoint) {
    ++nrOfData;
    sum += aDataPoint;
    min = Math.min(min, aDataPoint);
    max = Math.max(max, aDataPoint);
  }

  public float getAverage() {
    return (float)sum / nrOfData;
  }

  public void reset() {
    nrOfData = 0;
    sum      = 0;
    min      = Float.MAX_VALUE;
    max      = Float.MIN_VALUE;
  }

  public String toString() {
    return String.format("Avg=%s, min=%s, max=%s", getAverage(), getMin(), getMax());
  }

  public float getMin() {
    return min;
  }

  public float getMax() {
    return max;
  }

}

