/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Prefix trait.
 */

package pistache.picalculus

/** An object providing helper methods to pi-Calculus prefixes.
 */
object Prefix {

	/** Concatenation operator.
	 *
	 *  @param prefix the prefix.
	 *  @return the agent for this prefix.
	 */
	 implicit def PrefixToAgent(prefix: Prefix):ConcatenationAgent = ConcatenationAgent(() => prefix, NilAgent)
  
}

/** A trait representing pi-Calculus prefixes.
 */
trait Prefix extends PiObject {
  
	/** Concatenation operator.
	 *
	 *  @param other the other prefix.
	 *  @return the agent constructed by concatenation of this prefix and the given prefix.
	 */
	def *(other: => Prefix) = ConcatenationPrefix(() => this, other _)
 
	/** Concatenation operator.
	 *
	 *  @param other the agent.
	 *  @return the agent constructed by concatenation of this prefix and the given agent.
	 */
	def *(other: => Agent) = ConcatenationAgent(() => this, other _)
}

/** A class representing a prefix constructed by the concatenation of two prefixes.
 * 
 *  @param left the first prefix.
 *  @param right the second prefix.
 *  @return the constructed prefix.
 */
case class ConcatenationPrefix(val left: () => Prefix, val right: () => Prefix) extends Prefix