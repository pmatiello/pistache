/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Process class and companion object.
 */

package pistache.picalculus

/** An object providing methods to create pi-Calculus
 *  processes.
 */
object Process {
  
	/** Create a process.
	 *
	 *  @param process the process.
	 *  @return the process. 
	 */
	def apply(process: => Process) = new RestrictedProcess(process)

}

/** A trait representing pi-Calculus processes.
 */
protected[pistache] trait Process {
  
	/** Concatenation operator.
	 *
	 *  @param the other Other process.
	 *  @return the process constructed by concatenation of this
	 *  process and the given process.
	 */
	def *(other: => Process) = new ConcatenationProcess(this, other)
 
 	/** Composition operator.
	 *
	 *  @param the other Other process.
	 *  @return the process constructed by parallel composition of this
	 *  process and the given process.
	 */
  	def |(other: => Process) = new CompositionProcess(this, other)
}

/** A class representing pi-Calculus processes allowing restricted
 *  names.
 * 
 *  @param P the process.
 */
protected[pistache] class RestrictedProcess(P: => Process) extends Process {
	
	/** the process */
	val process = P _ 
  
}

/** A class representing a process constructed by the concatenation
 *  of two other processes.
 * 
 *  @param P the first process.
 *  @param Q the second process.
 *  @return the constructed process.
 */
protected[pistache] class ConcatenationProcess(P: => Process, Q: => Process) extends Process {
	lazy val left = P
	lazy val right = Q
}

/** A class representing a process constructed by the composition
 *  of two other processes.
 * 
 *  @param P the first process.
 *  @param Q the second process.
 *  @return the constructed process.
 */
protected[pistache] class CompositionProcess(P: => Process, Q: => Process) extends Process {
	lazy val left = P
	lazy val right = Q
}