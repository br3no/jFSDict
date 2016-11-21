package de.reffle.jfsdict.runtime;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import de.reffle.jfsdict.dictionary.*;
import de.reffle.jfsdict.levenshtein.FuzzyDictSearch;
import de.reffle.jfsdict.levenshtein.MatchReceiverList;
import de.reffle.jfsdict.util.Stats;
import de.reffle.jfsdict.util.Stopwatch;
import de.reffle.jfsdict.util.test.RandomErrors;

public class FuzzyDictSearchRuntimeTest {

  private static final Logger LOG = Logger.getLogger(DictRuntimeTest.class.getName());

  @Test
  public void testRuntime() throws Exception {
    Dictionary minDic = getMinDic();

    Random random = new Random(42);

    PatternGenerator patternGenerator = new PatternGenerator("/english_modern.lex", random);

    FuzzyDictSearch fuzzyDictSearch = new FuzzyDictSearch(minDic);

    int distance    = 1;
    int nrOfQueries = 10000;

    Stopwatch stopwatchAll = new Stopwatch();
    Stopwatch stopwatch = new Stopwatch();
    MatchReceiverList matchReceiver = new MatchReceiverList();
    Stats statsNrOfMatches = new Stats();
    Stats statsDuration    = new Stats();
    for(int i = 0; i<nrOfQueries; ++i) {
      String pattern = patternGenerator.nextQuery(distance);
      stopwatch.reset();
      matchReceiver.clear();
      fuzzyDictSearch.query(pattern, distance, matchReceiver);
      int nrOfMatches = matchReceiver.getList().size();
      statsNrOfMatches.put(nrOfMatches);
      statsDuration.put(stopwatch.getMillis());
    }
    LOG.log(Level.INFO,
        "{0} queries with distance {1}: {2} ms",
        new Object[]{nrOfQueries, distance, stopwatchAll.getMillis()});
    LOG.log(Level.INFO, "Number of matches: {0}", statsNrOfMatches.toString());
    LOG.log(Level.INFO, "duration: {0}", statsDuration.toString());
  }


  private Dictionary getMinDic() throws FileNotFoundException, IOException {
    MinDicBuilder minDicBuilder = new MinDicBuilder();
    InputStream testFileStream = this.getClass().getResourceAsStream("/english_modern.lex");
    return minDicBuilder.buildFromWordlist(testFileStream);
  }

  class PatternGenerator {
    List<DictEntry> entries;
    private Random random;
    RandomErrors randomErrors;

    public PatternGenerator(String aResource, Random aRandom) throws IOException {
      random = aRandom;
      randomErrors = new RandomErrors(random);
      entries = getEntries("/english_modern.lex");
    }

    String nextQuery(int aNrOfOperations) {
      DictEntry entry = entries.get(random.nextInt(entries.size()));
      String pattern = entry.getKey();
      randomErrors.addErrors(pattern, aNrOfOperations);
      return pattern;
    }

    private List<DictEntry> getEntries(String name) throws IOException {
      InputStream testFileStream = this.getClass().getResourceAsStream(name);
      InputStreamReader reader = new InputStreamReader(testFileStream);
      BufferedReader bufReader = new BufferedReader(reader);
      String line;
      List<DictEntry> entries = new ArrayList<>();
      while((line = bufReader.readLine()) != null) {
        DictEntry entry = DictionaryBuilder.parseEntry(line);
        entries.add(entry);
      }
      return entries;
    }

  }

}
