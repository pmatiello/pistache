/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A pi-Calculus specification runner using threads.
 */

package pistache.runner

import pistache.picalculus._

class ThreadedRunner(process:Process) {
  
	def start = run(process)
  
	def run(process:Process) {
		process match {
			case proc:Transition => proc.procedure apply
			case proc:ConcatenationProcess => run(proc left)
											  run(proc right)
		}
	}
  
}
