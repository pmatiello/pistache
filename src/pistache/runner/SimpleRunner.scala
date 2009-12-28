/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A simple and naive runner for pi-Calculus processes.
 */

package pistache.runner

import pistache.picalculus._

class SimpleRunner(process:Process) {
  
	val p = process
 
	def start { run(p) }
 
	private def run(p:Process) {
		
		p match {
			case pp:ConcatenationProcess => run(pp left)
		  									run(pp right)
			case pp:IfProcess => if (pp.condition apply) run(pp then)
			case pp:IfElseProcess => if (pp.condition apply) run(pp then) else run(pp elseThen)
			case pp:Transition => pp.procedure apply 
		}
	}

}
