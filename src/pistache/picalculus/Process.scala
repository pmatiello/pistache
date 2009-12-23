/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Process class and companion object.
 */

package pistache.picalculus

abstract class Process {
	 val description:List[Process]
  
	 def +(process:Process):Process = new SumProcess(this, process)
}

class SumProcess(P:Process, Q:Process) extends Process {
	val description = P :: Q :: Nil
}