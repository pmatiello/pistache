/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Transition class and companion object.
 */

package pistache.picalculus

object Transition {
  
	def apply(action: => Unit) = new Transition(action)
  
}

protected class Transition(action: => Unit) extends Process {
	
	private val procedure = action _;
 
	val description = null;
 
	def run = procedure.apply
  
}
