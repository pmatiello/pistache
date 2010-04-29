/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Prefix trait.
 */

package pistache.picalculus

/** An object providing helper methods to pi-Calculus prefixes.
 */
object Prefix {
  
	 implicit def PiObjectToAgent(piObject: PiObject) = PrefixAgent(piObject)
  
}

/** A trait representing pi-Calculus prefixes.
 */
trait Prefix extends PiObject {
  
	/** Concatenation operator.
	 *
	 *  @param other the other agent.
	 *  @return the agent constructed by concatenation of this agent and the given agent.
	 */
	def *(other: => Prefix) = ConcatenationPrefix(() => this, other _)
 
	/** Concatenation operator.
	 *
	 *  @param other the other agent.
	 *  @return the agent constructed by concatenation of this agent and the given agent.
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
