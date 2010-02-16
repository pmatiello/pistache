/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Agent class and companion object.
 */

package pistache.picalculus

/** An object providing methods to create pi-Calculus agents.
 */
object Agent {
  
	/** Create a agent.
	 *
	 *  @param agent the agent.
	 *  @return the agent. 
	 */
	def apply(agent: => Agent) = new RestrictedAgent(agent)

}

/** A trait representing pi-Calculus agents.
 */
protected[pistache] trait Agent {
  
	/** Concatenation operator.
	 *
	 *  @param other the other agent.
	 *  @return the agent constructed by concatenation of this agent and the given agent.
	 */
	def *(other: => Agent) = new ConcatenationAgent(this, other)
 
 	/** Composition operator.
	 *
	 *  @param other the other agent.
	 *  @return the agent constructed by parallel composition of this agent and the given agent.
	 */
  	def |(other: => Agent) = new CompositionAgent(this, other)
}

/** A class representing pi-Calculus agents allowing restricted agents.
 * 
 *  @param P the agent.
 */
protected[pistache] class RestrictedAgent(P: => Agent) extends Agent {
	
	/** the agent */
	val agent = P _ 
  
}

/** A class representing a agent constructed by the concatenation of two other agents.
 * 
 *  @param P the first agent.
 *  @param Q the second agent.
 *  @return the constructed agent.
 */
protected[pistache] class ConcatenationAgent(P: => Agent, Q: => Agent) extends Agent {
  
	/** the first agent */
	lazy val left = P
 
	/** the second agent */
	lazy val right = Q
}

/** A class representing a agent constructed by the composition of two other agents.
 * 
 *  @param P the first agent.
 *  @param Q the second agent.
 *  @return the constructed agent.
 */
protected[pistache] class CompositionAgent(P: => Agent, Q: => Agent) extends Agent {
	
	/** the first agent */
	lazy val left = P
 
	/** the second agent */
	lazy val right = Q
}