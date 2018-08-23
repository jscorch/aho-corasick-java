package jscorch.string.matching.ahocorasick;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import jscorch.string.matching.PatternMatchOutputProcessor;
import jscorch.string.matching.PatternMatcher;
import jscorch.string.matching.PatternMatcherFactory;

/**
 * A factory for creating <code>PatternMatcher</code> objects which implement
 * the Aho-Corasick string matching algorithm. The factory maintains state of
 * various keywords added, removed, and cleared in total. Any
 * <code>PatternMatcher</code> created by the factory will represent the state
 * of the factory and its keywords at the time of creation.
 * 
 * See {@link PatternMatcherFactory} for documentation on methods
 */
public class AhoCorasickPatternMatcherFactory implements PatternMatcherFactory {
  private AhoCorasickPatternMatcher matcher;

  public AhoCorasickPatternMatcherFactory() {
    matcher = new AhoCorasickPatternMatcher();
  }

  @Override
  public boolean addKeyword(String keyword) {
    return matcher.addKeyword(keyword);
  }

  @Override
  public boolean removeKeyword(String keyword) {
    return matcher.removeKeyword(keyword);
  }

  @Override
  public void clear() {
    matcher.clear();
  }

  @Override
  public PatternMatcher createPatternMatcher() {
    AhoCorasickPatternMatcher created = new AhoCorasickPatternMatcher(matcher);
    created.completeSetup();
    return created;
  }

  class AhoCorasickPatternMatcher implements PatternMatcher {
    private State start;
    private State current;
    private int pos;
    private boolean keepProcessing;
    private HashMap<State, State> failFunction = new HashMap<>();

    private PatternMatchOutputProcessor processor;

    private AhoCorasickPatternMatcher() {
      start = new StartState();
      current = start;
    }

    private AhoCorasickPatternMatcher(AhoCorasickPatternMatcher matcher) {
      start = matcher.start.copy();
      current = start;
    }

    private boolean addKeyword(String keyword) {
      return addKeyword(start, keyword, 0);
    }

    private boolean addKeyword(State state, String keyword, int ndx) {
      if (ndx == keyword.length()) {
        return state.output.add(keyword);
      }

      char c = keyword.charAt(ndx);
      state.ensureGoto(c);
      return addKeyword(state.getGoto(c), keyword, ndx + 1);
    }

    private boolean removeKeyword(String keyword) {
      return removeKeyword(start, keyword, 0);
    }

    private boolean removeKeyword(State state, String keyword, int ndx) {
      if (ndx == keyword.length()) {
        return state.output.remove(keyword);
      }

      boolean removed = false;

      char c = keyword.charAt(ndx);
      State next = state.getGoto(c);

      if (next != null) {
        removed = removeKeyword(next, keyword, ndx + 1);
        if (removed && !next.hasGotos() && next.output.isEmpty()) {
          state.clearGoto(c);
        }
      }

      return removed;
    }

    private void clear() {
      start.gotoFunction.clear();
      failFunction.clear();
    }

    private void completeSetup() {
      LinkedList<State> queue = new LinkedList<>();

      for (State state : start.getGotoStates()) {
        failFunction.put(state, start);
        queue.addLast(state);
      }

      while (!queue.isEmpty()) {
        State state = queue.removeFirst();
        computeFailFunction(state);
        queue.addAll(state.getGotoStates());
      }

    }

    private void computeFailFunction(State state) {
      for (char c : state.getGotoChars()) {
        State fail = failFunction.get(state);

        while (fail.getGoto(c) == null) {
          fail = failFunction.get(fail);
        }

        failFunction.put(state.getGoto(c), fail.getGoto(c));
        state.getGoto(c).output.addAll(fail.getGoto(c).output);
      }
    }

    @Override
    public void processStream(Reader reader, PatternMatchOutputProcessor processor) throws IOException, IllegalStateException {
      synchronized (this) {
        if (keepProcessing) {
          throw new IllegalStateException("cannot call process stream until previous invocation completes");
        }

        keepProcessing = true;
      }

      this.processor = processor;

      pos = 0;
      current = start;

      int c;

      try {
        while ((c = reader.read()) != -1) {
          synchronized (this) {
            if (!keepProcessing) {
              processor.done();
              return;
            }
          }

          processInput((char) c);
        }
      } catch (IOException ex) {
        keepProcessing = false;
        throw ex;
      }

      keepProcessing = false;
      processor.done();
    }

    @Override
    public synchronized void stop() {
      keepProcessing = false;
    }

    private void processInput(char c) {
      State next = current.getGoto(c);

      if (next == null) {
        State fail = failFunction.get(current);
        while (fail.getGoto(c) == null) {
          fail = failFunction.get(fail);
        }

        next = fail.getGoto(c);
      }

      if (next.hasOutput()) {
        processor.patternFound(new ArrayList<String>(next.output), pos);
      }

      current = next;
      pos++;
    }

    private class State {
      HashMap<Character, State> gotoFunction = new HashMap<>();
      HashSet<String> output = new HashSet<>();

      public void ensureGoto(char c) {
        gotoFunction.computeIfAbsent(c, func -> new State());
      }

      public void setGoto(char c, State state) {
        gotoFunction.put(c, state);
      }

      public State getGoto(char c) {
        return gotoFunction.get(c);
      }

      public void clearGoto(char c) {
        gotoFunction.remove(c);
      }

      public boolean hasGotos() {
        return !gotoFunction.isEmpty();
      }

      public boolean hasOutput() {
        return !output.isEmpty();
      }

      public Set<Character> getGotoChars() {
        return gotoFunction.keySet();
      }

      public Set<State> getGotoStates() {
        return new HashSet<State>(gotoFunction.values());
      }

      public State copy() {
        State copy = new State();
        copy.output.addAll(output);
        for (char c : getGotoChars()) {
          copy.setGoto(c, getGoto(c).copy());
        }

        return copy;
      }
    }

    private class StartState extends State {
      @Override
      public State getGoto(char c) {
        return gotoFunction.getOrDefault(c, this);
      }

      @Override
      public State copy() {
        StartState copy = new StartState();
        copy.output.addAll(output);
        for (char c : getGotoChars()) {
          copy.setGoto(c, getGoto(c).copy());
        }

        return copy;
      }
    }

  }

}
