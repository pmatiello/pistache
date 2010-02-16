/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * If object and companion classes.
 */

package pistache.picalculus

/** An object providing methods to create pi-Calculus conditional agents.
 */
object If {
  
	 /** Create a conditional agent.
	  * 
	  *  @param condition the condition.
	  *  @agent the agent.
	  */
	 def apply(condition: => Boolean)(agent: Agent) = new IfAgent(condition, agent)

}

/** A class representing pi-Calculus conditional operator If.
 * 
 *  @param cond the condition.
 *  @param agent the agent to be executed if <code>condition</code> evaluates to <code>true</code>.
 */
protected[pistache] class IfAgent(cond: => Boolean, agent: => Agent) extends Agent {
	
	/** Condition. */
	val condition = cond _
 
	/** Agent to be executed if <code>condition</code> evaluates to <code>true</code>. */
	val then = agent	

	/** Create another conditional agent.
	 * 
	 *  @param agent the agent to be executed if <code>condition</code> evaluates to <code>false</code>. 
	 */
	def Else(agent:Agent) = new IfElseAgent(cond, then, agent) 

}

/** A class representing pi-Calculus conditional operator If with an Else branch.
 * 
 *  @param cond the condition.
 *  @param yes the agent to be executed if <code>condition</code> evaluates to <code>true</code>.
 *  @param no the agent to be executed if <code>condition</code> evaluates to <code>false</code>.
 */
protected[pistache] class IfElseAgent(cond: => Boolean, yes: => Agent, no: => Agent) extends Agent {
	
	/** Condition. */
	val condition = cond _
 
	/** Agent to be executed if <code>condition</code> evaluates to <code>true</code>. */
	val then = yes
 
	/** Agent to be executed if <code>condition</code> evaluates to <code>false</code>. */
	val elseThen = no

}