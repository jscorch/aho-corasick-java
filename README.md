# aho-corasick-java
Java implementation of Aho-Corasick string matching algorithm

## Introduction

A Java implementation of the Aho-Corasick string matching algorithm. Aho-Corasick can locate all occurences of any number of substrings (referred to as keywords) in a character stream in a single pass of the stream. 

[Aho-Corasick algorithm on Wikipedia](https://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_algorithm)

## Usage

Use an AhoCorasickPatternFactory to configure then create a PatternMatcher.
The AhoCorasickPatternFactory can be used to create multiple PatternMatchers and retains its state after creating a
PatternMatcher. This makes it easy to create many similar or related PatternMatchers.

```
PatternMatcherFactory factory = new AhoCorasickPatternMatcherFactory();
factory.addKeyword("the");
factory.addKeyword("quick");

// matcher will recognize keywords "the", "quick"
PatternMatcher matcher = factory.createPatternMatcher();

factory.addKeyword("brown");
factory.addKeyword("fox");
factory.addKeyword("jumped");

// matcher2 will recognize keywords "the", "quick", "brown", "fox", "jumped"
PatternMatcher matcher2 = factory.createPatternMatcher();

factory.removeKeyword("quick");
factory.removeKeyword("brown");

// matcher3 will recognize keywords "the", "fox", "jumped"
PatternMatcher matcher3 = factory.createPatternMatcher();
```

Once you've created a PatternMatcher object, you can call its `processStream()` method to search a character stream for the keywords the PatternMatcher recognizes. The `processStream()` method takes two parameters, a Reader object, which is a class for reading character streams, and a PatternMatchOutputProcessor object, whose methods get called back by the PatternMatcher to report on its progress. When the PatternMatcher locates one or more keywords at any point in the character stream, it will call the PatternMatchOutputProcessor `patternFound()` method, passing the list of keywords it found and at what position in the stream (where the keyword(s) end).

```
PatternMatcher matcher = factory.createPatternMatcher();
Reader reader = new StringReader("the quick brown fox jumped over the lazy dog");
PatternMatchOutputProcessor outputProcessor = new PatternMatchOutputProcessor() {

  public void patternFound(List<String> keywords, int endPos) {
    System.out.println(keywords + " found ending at pos " + endPos);
  }

  public void done() {
    System.out.println("character stream fully processed");
  }
};

try {
  matcher.processStream(reader, outputProcessor);
}
catch(IOException ex) {
  ex.printStackTrace();
}
```

Once `processStream()` has been called on the PatternMatcher, it will return in one of three ways:
- It will fully process the stream, calling `done()` on the PatternMatchOutputProcessor passed it, and then return.
- `stop()` is called on the PatternMatcher. Note that unless this is done from a separate thread, the only opportunity to call `stop()` will be when the patternFound() method of PatternMatchOutputProcessor is called back.
- `processStream()` throws an IOException while reading the character stream it's been called with.

Once `processStream()` returns, you can call it again to process another character stream. The PatternMatcher will reset itself, including the stream position it reports.

## Notes
- This code is still a work in progress
- AhoCorasickPatternMatcher is case sensitive
- Look at the sample code in Test.java to see more examples of usage

## To-do
- [ ] create Maven project file
- [ ] add more code to Test.java
- [ ] consider any suggestions

