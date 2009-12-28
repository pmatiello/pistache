/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Process class and companion object.
 */

package pistache.picalculus

object Process {
  
	def apply(process: => Process):Process = {
		lazy val proc = process
		proc.self = proc
		return proc
	}

}

object self extends Process

/** A class representing pi-Calculus processes.
 */
protected[pistache] class Process {

	var self:Process = null
  
	/** Concatenation operator.
	 *
	 *  @param the other Other process.
	 *  @return the process constructed by concatenation of this
	 *  process and the given process.
	 */
	def *(other: => Process) = new ConcatenationProcess(this, other)
  
	/** Summation operator.
	 *
	 *  @param the other Other process.
	 *  @return the process formed by summation of this
	 *  process and the given process.
	 */
 	def +(other: => Process) = new SumProcess(this, other)
 
 	/** Composition operator.
	 *
	 *  @param the other Other process.
	 *  @return the process constructed by parallel composition of this
	 *  process and the given process.
	 */
  	def |(other: => Process) = new CompositionProcess(this, other)
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

/** A class representing a process constructed by the summation
 *  of two other processes.
 * 
 *  @param P the first process.
 *  @param Q the second process.
 *  @return the constructed process.
 */
protected[pistache] class SumProcess(P: => Process, Q: => Process) extends Process {
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