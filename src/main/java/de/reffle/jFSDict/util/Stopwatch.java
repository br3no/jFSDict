package de.reffle.jFSDict.util;


public class Stopwatch {

  long startNanos;

  public Stopwatch() {
    reset();
  }

  public void reset() {
    startNanos = now();
  }

  public long getMillies() {
    return getNanos() / 1000000;
  }

  private long now() {
    return System.nanoTime();
  }

  public long getNanos() {
    return now() - startNanos;
  }


}
