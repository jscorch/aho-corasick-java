package jscorch.string.matching;

/**
 * An object which creates a <code>PatternMatcher</code>
 *
 */

public interface PatternMatcherFactory {

  /**
   * Adds a keyword to the list of keywords any <code>PatternMatcher</code>
   * created by the factory will recognize
   * 
   * @param keyword
   * @return true if keyword didn't exist and was added, false otherwise
   */

  public boolean addKeyword(String keyword);

  /**
   * Removes a keyword from the list of keywords any <code>PatternMatcher</code>
   * created by the factory will recognize
   * 
   * @param keyword
   * @return true if keyword existed and was removed, false otherwise
   */
  public boolean removeKeyword(String keyword);

  /**
   * Clears all keywords from the list of keywords any <code>PatternMatcher</code>
   * created by the factory will recognize
   */
  public void clear();

  /**
   * Returns a newly created <code>PatternMatcher</code> that will recognize all
   * keywords specified to the factory
   * 
   * @return the newly created <code>PatternMatcher</code>
   */
  public PatternMatcher createPatternMatcher();
}
