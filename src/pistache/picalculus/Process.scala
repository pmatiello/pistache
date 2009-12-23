/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Process class and companion object.
 */

package pistache.picalculus

abstract class Process {
	val description:Process
  
	def *(other:Process):Process = new ConcatenationProcess(this, other)
  
	def +(other:Process):Process = new SumProcess(this, other)
}

case class ConcatenationProcess(P:Process, Q:Process) extends Process {
	val description = null
}

case class SumProcess(P:Process, Q:Process) extends Process {
	val description = null
}