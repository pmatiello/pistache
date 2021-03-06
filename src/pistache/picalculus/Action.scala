/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Transition class and companion object.
 */

package pistache.picalculus

/** An object providing methods to create silent actions.
 */
object Action {
  
	/** Create a silent action.
	 *
	 *  @param function the transition procedure.
	 *  @return the action. 
	 */
	def apply(function: => Unit) = ActionPrefix(function _)
  
}

/** A class representing pi-Calculus silent actions.
 * 
 *  @param function the transition procedure.
 */
case class ActionPrefix(val procedure: () => Unit) extends Prefix
