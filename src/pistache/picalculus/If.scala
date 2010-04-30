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
	 def apply(condition: => Boolean)(agent: => Agent) = new MatchAgent(condition _, () => agent)

}

/** A class representing pi-Calculus conditional operator If.
 * 
 *  @param condition the condition.
 *  @param then the agent to be executed if <code>condition</code> evaluates to <code>true</code>.
 */
case class MatchAgent(val condition: () => Boolean, val then: () => Agent) extends Agent