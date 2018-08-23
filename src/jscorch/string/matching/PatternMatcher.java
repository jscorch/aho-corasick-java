package jscorch.string.matching;

import java.io.IOException;
import java.io.Reader;

/**
 * An object that searches a character stream for specified keywords
 *
 */

public interface PatternMatcher {

  /**
   * Causes the given character stream <code>reader</code> to be searched for
   * specified keywords beginning at its current position
   * 
   * @param reader
   *          - supplies the character stream to be searched
   * @param processor
   *          - specifies callback methods PatternMatcher will call to specify various events
   * @throws IOException
   *           - thrown if an error occurs reading character stream supplied by <code>reader</code>
   * @throws IllegalStateException
   *           - thrown if processStream() is called when previous invocation of processStream() hasn't returned
   */
  public void processStream(Reader reader, PatternMatchOutputProcessor processor) throws IOException, IllegalStateException;

  /**
   * Causes the <code>PatternMatcher</code> to stop processing the character
   * stream supplied to <code>processStream</code>
   */
  public void stop();
}
