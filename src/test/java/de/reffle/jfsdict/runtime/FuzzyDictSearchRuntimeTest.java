package de.reffle.jfsdict.runtime;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.reffle.jfsdict.dictionary.*;
import de.reffle.jfsdict.levenshtein.FuzzyDictSearch;
import de.reffle.jfsdict.levenshtein.MatchReceiverList;
import de.reffle.jfsdict.util.Stats;
import de.reffle.jfsdict.util.Stopwatch;
import de.reffle.jfsdict.util.test.RandomErrors;

public class FuzzyDictSearchRuntimeTest {

  private static Logger LOG = LoggerFactory.getLogger(FuzzyDictSearchRuntimeTest.class);

  Random random = new Random(42);

  private Dictionary       minDic;
  private PatternGenerator patternGenerator;

  public FuzzyDictSearchRuntimeTest() throws Exception {
    minDic = getMinDic();
    patternGenerator = new PatternGenerator("/english_modern.lex", random);
  }

  @Test
  public void testRuntime() throws Exception {
    testRuntime(1, 50000);
    testRuntime(2, 5000);
  }

  public void testRuntime(int aDistance, int aNrOfQueries) throws Exception {


    FuzzyDictSearch fuzzyDictSearch = new FuzzyDictSearch(minDic);

    Stopwatch stopwatchAll = new Stopwatch();
    Stopwatch stopwatch = new Stopwatch();
    MatchReceiverList matchReceiver = new MatchReceiverList();
    Stats statsNrOfMatches = new Stats();
    Stats statsDuration    = new Stats();
    for(int i = 0; i<aNrOfQueries; ++i) {
      String pattern = patternGenerator.nextQuery(aDistance);
      stopwatch.reset();
      matchReceiver.clear();
      fuzzyDictSearch.query(pattern, aDistance, matchReceiver);
      int nrOfMatches = matchReceiver.getList().size();
      statsNrOfMatches.put(nrOfMatches);
      statsDuration.put(stopwatch.getMillis());
    }
    LOG.info("{} queries with distance {}: {} ms", aNrOfQueries, aDistance, stopwatchAll.getMillis());
    LOG.info("Number of matches: {}", statsNrOfMatches.toString());
    LOG.info("duration (ms): {}", statsDuration.toString());
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
