/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Transition class and companion object.
 */

package pistache.picalculus

/** An object providing methods to create silent transitions.
 */
object Transition {
  
	def apply(action: => Unit) = new Transition(action)
  
}

/** A class representing pi-Calculus silent transitions.
 * 
 *  @param action the transition procedure.
 */
protected[pistache] class Transition(action: => Unit) extends Process {
	
	/** Transition procedure. */
	val procedure = action _;
 
}
