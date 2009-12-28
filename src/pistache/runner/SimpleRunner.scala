/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A simple and naive runner for pi-Calculus processes.
 * 
 * This is only for some testing and exploration of ideas and
 * should be replaced with something properly written soon.
 */

package pistache.runner

import pistache.picalculus._
import scala.collection.mutable.HashMap

class SimpleRunner(process:Process) {
  
	val p = process
 
	var links:HashMap[Link[_], Any] = null
 
	def start() {
		continue(new HashMap[Link[_], Any])
	}
 
	def continue(links:HashMap[Link[_], Any]) {
		this.links = links
		run(p)
	}
 
	private def run(p:Process) {
		
		p match {
			case pp:ConcatenationProcess => run(pp left)
		  									run(pp right)
			case pp:IfProcess => if (pp.condition apply) run(pp then)
			case pp:IfElseProcess => if (pp.condition apply) run(pp then) else run(pp elseThen)
			case pp:Transition => pp.procedure apply
			case pp:CompositionProcess => { // won't work in many cases
				new Thread() {
					override def run() { new SimpleRunner(pp.left).continue (links) }
				}.start
				new Thread() {
					override def run() { new SimpleRunner(pp.right).continue (links) }
				}.start
			}
			case pp:LinkProcess[_] => {	// obviously thread-unsafe
				if (!links.keySet.contains(pp.link)) {
					links += pp.link -> null
				}
				if (pp.action == Link.Action.Send) {
					while (links(pp.link) != null) {}
					links(pp.link) = pp.name.value
				} else {
					while (links(pp.link) == null) {}
					pp.asInstanceOf[LinkProcess[Any]].name := links(pp.link)
					links(pp.link) = null
				}
			}
		}
	}

}
