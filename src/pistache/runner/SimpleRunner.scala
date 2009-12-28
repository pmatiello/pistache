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
		  	case ConcatenationProcess(first, second) => run(first)
		  												run(second)
		  	case pp:IfProcess => if (pp.condition apply) run(pp description)
		  	case pp:IfElseProcess => if (pp.condition apply) run(pp description) else run(pp descriptionElse)
		  	case pp:Transition => pp.procedure apply
			case _ => run(p description)
		}
	}

}
