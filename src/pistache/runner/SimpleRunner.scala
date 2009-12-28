/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A simple and naive runner for pi-Calculus processes.
 */

package pistache.runner

import pistache.picalculus._

class SimpleRunner(process:Process) {
  
	val p = process
 
	def start { run(p, null) }
 
	private def run(p:Process, oldself:Process) {
		val newself = if (p.self != null) p.self else oldself
		
		p match {
			case pp:ConcatenationProcess => run(pp left, newself)
		  									run(pp right, newself)
			case pp:IfProcess => if (pp.condition apply) run(pp then, newself)
			case pp:IfElseProcess => if (pp.condition apply) run(pp then, newself) else run(pp elseThen, newself)
			case pp:Transition => pp.procedure apply
			case self => run(newself, newself) 
		}
	}

}
