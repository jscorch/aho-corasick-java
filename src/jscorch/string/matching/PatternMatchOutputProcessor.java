package jscorch.string.matching;

import java.util.List;

/**
 * An object that specifies callback methods that <code>PatternMatcher</code>
 * will use to report on its progress
 * 
 */
public interface PatternMatchOutputProcessor {

  /**
   * Called to indicate <code>PatternMatcher</code> has found one or more keywords
   * in the character stream it is processing
   * 
   * @param keywords
   *          - list of one or more keywords found
   * @param endPos
   *          - the position in the stream (relative to stream position when
   *          <code>processStream</code> was invoked on<code>PatternMatcher</code>) 
   *          where the keyword(s) final character is located
   */
  public void patternFound(List<String> keywords, int endPos);

  /**
   * Called to indicate <code>PatternMatcher</code> has reached the end of
   * supplied character stream
   */
  public void done();
}
