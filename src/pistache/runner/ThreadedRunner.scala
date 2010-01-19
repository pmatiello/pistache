/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A pi-Calculus specification runner using threads.
 */

package pistache.runner

import pistache.picalculus._

/** A local, multithreaded runner for pi-Calculus processes.
 * 
 *  @param process the process to be executed.
 */
class ThreadedRunner(process:Process) {
  
	/** Start the execution of the process.
	 */
	def start = run(process)
  
	/** Run a given process.
	 *
	 *  @param process the process to be executed.
	 */
	private def run(process:Process) {
		process match {
		  
			/* Execute action */
			case proc:Action => proc.procedure apply

			/* Execute processes sequentially */
			case proc:ConcatenationProcess => run(proc left)
											  run(proc right)
            
			/* Execute processes in parallel */
            case proc:CompositionProcess => {
              
            	val leftThread = new Thread() {
            		override def run() { new ThreadedRunner(proc.left) start}
            	}               
            	val rightThread = new Thread() {
            		override def run() { new ThreadedRunner(proc.right) start}
            	}
             
            	leftThread.start
            	rightThread.start
            	leftThread.join
            	rightThread.join
               
            }
            
            /* Execute processes conditionally */
			case proc:IfProcess => if (proc.condition apply) run(proc then)
			case proc:IfElseProcess => if (proc.condition apply) run(proc then) else run(proc elseThen)
		}
	}
  
}
