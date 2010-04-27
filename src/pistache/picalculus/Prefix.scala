/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Prefix trait.
 */

package pistache.picalculus

/** A trait representing pi-Calculus prefixes.
 */
trait Prefix extends PiObject {
  
	/** Concatenation operator.
	 *
	 *  @param other the other agent.
	 *  @return the agent constructed by concatenation of this agent and the given agent.
	 */
	def *(other: => Agent) = ConcatenationAgent(() => this, other _)

}
