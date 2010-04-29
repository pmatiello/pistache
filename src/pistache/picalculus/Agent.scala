/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Agent class and companion object.
 */

package pistache.picalculus

import Implicits._

/** An object providing methods to create pi-Calculus agents.
 */
object Agent {
  
	/** Create a agent.
	 *
	 *  @param agent the agent.
	 *  @return the agent. 
	 */
	def apply(agent: => Agent) = RestrictedAgent(agent _)

}

/** A trait representing pi-Calculus agents.
 */
trait Agent extends PiObject {
 
 	/** Composition operator.
	 *
	 *  @param other the other agent.
	 *  @return the agent constructed by parallel composition of this agent and the given agent.
	 */
  	def |(other: => Agent) = CompositionAgent(() => this, other _)
}

/** A class representing pi-Calculus agents allowing restricted agents.
 * 
 *  @param agent the agent.
 */
case class RestrictedAgent(val agent: () => Agent) extends Agent

/** A class representing a agent constructed by the concatenation of a prefix and an agent.
 * 
 *  @param left the prefix.
 *  @param right the agent.
 *  @return the constructed agent.
 */
case class ConcatenationAgent(val left: () => Prefix, val right: () => Agent) extends Agent

/** A class representing a agent constructed by the composition of two other agents.
 * 
 *  @param left the first agent.
 *  @param right the second agent.
 *  @return the constructed agent.
 */
case class CompositionAgent(val left: () => Agent, val right: () => Agent) extends Agent