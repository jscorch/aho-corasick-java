package jscorch.string.matching.ahocorasick.test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import jscorch.string.matching.PatternMatchOutputProcessor;
import jscorch.string.matching.PatternMatcher;
import jscorch.string.matching.PatternMatcherFactory;
import jscorch.string.matching.ahocorasick.AhoCorasickPatternMatcherFactory;

public class Test {

  static class OutputProcessor implements PatternMatchOutputProcessor {

    @Override
    public void patternFound(List<String> keywords, int endPos) {
      System.out.println(keywords + " found ending at position " + endPos);
    }

    @Override
    public void done() {
      System.out.println("character stream fully processed");
    }
  }

  private static void runTest() throws Exception {
    PatternMatcherFactory factory = new AhoCorasickPatternMatcherFactory();
    factory.addKeyword("quick");
    factory.addKeyword("the quick");
    factory.addKeyword("fox");
    factory.addKeyword("brown fox");
    factory.addKeyword("quick brown fox");

    PatternMatcher matcher = factory.createPatternMatcher();
    Reader reader = new StringReader("the quick brown fox jumped over the lazy dog");
    OutputProcessor outputProcessor = new OutputProcessor();

    matcher.processStream(reader, outputProcessor);

    factory.clear();
    factory.addKeyword("lazy");
    factory.addKeyword("dog");
    factory.addKeyword("the lazy dog");
    matcher = factory.createPatternMatcher();

    // obviously, this file needs to exist
    reader = new FileReader(new File("./input/quick-brown-fox.txt"));
    matcher.processStream(reader, outputProcessor);
    reader.close();

    factory.removeKeyword("lazy");
    matcher = factory.createPatternMatcher();

    reader = new FileReader(new File("./input/quick-brown-fox.txt"));
    matcher.processStream(reader, outputProcessor);
    reader.close();
  }

  public static void main(String[] args) {
    try {
      Test.runTest();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
