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
	 def apply(condition: => Boolean)(agent: => Agent) = new IfAgent(condition _, () => agent)

}

/** A class representing pi-Calculus conditional operator If.
 * 
 *  @param condition the condition.
 *  @param then the agent to be executed if <code>condition</code> evaluates to <code>true</code>.
 */
protected[pistache] class IfAgent(val condition: () => Boolean, val then: () => Agent) extends Agent {

	/** Create another conditional agent.
	 * 
	 *  @param agent the agent to be executed if <code>condition</code> evaluates to <code>false</code>. 
	 */
	def Else(agent: => Agent) = new IfElseAgent(condition, then, () => agent)

}

/** A class representing pi-Calculus conditional operator If with an Else branch.
 * 
 *  @param condition the condition.
 *  @param then the agent to be executed if <code>condition</code> evaluates to <code>true</code>.
 *  @param elseThen the agent to be executed if <code>condition</code> evaluates to <code>false</code>.
 */
protected[pistache] class IfElseAgent(val condition: () => Boolean, val then: () => Agent, val elseThen: () => Agent) extends Agent