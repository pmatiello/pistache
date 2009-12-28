/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * If object and companion classes.
 */

package pistache.picalculus

/** An object providing methods to create pi-Calculus
 *  conditional processes.
 */
object If {
  
	def apply(condition: => Boolean)(process: Process) = new IfProcess(condition, process)

}

/** A class representing pi-Calculus conditional operator If.
 */
protected[pistache] class IfProcess(cond: => Boolean, process:Process) extends Process {
	
	/** Condition. */
	val condition = cond _
 
	/** Process to be executed if <code>condition</code> evaluates to <code>true</code>. */
	val description = process	

	def Else(process:Process) = new IfElseProcess(cond, description, process) 

}

/** A class representing pi-Calculus conditional operator Else.
 */
protected[pistache] class IfElseProcess(cond: => Boolean, yes:Process, no:Process) extends Process {
	
	/** Condition. */
	val condition = cond _
 
	/** Process to be executed if <code>condition</code> evaluates to <code>false</code>. */
	val description = yes
	val descriptionElse = no

}