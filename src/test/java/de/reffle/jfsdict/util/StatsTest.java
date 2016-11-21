package de.reffle.jfsdict.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import de.reffle.jfsdict.util.Stats;

public class StatsTest {
  @Test
  public void test() throws Exception {
    Stats stats = new Stats();

    stats.put(3);
    stats.put(6);
    stats.put(7);
    assertEquals(5.3333, stats.getAverage(), 0.001);
  }
}
