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

class LinkChannel(l:Link[_]) {
	 val link = l
	 private var isFilled = false
	 private var value:Any = null
	 var lock : AnyRef = new Object()
  
	 private def waitUntil(cond : => Boolean) {
		 while (!cond) { lock.wait }
	 }
  
	 def send(value:Any) {
		lock.synchronized {
			 waitUntil (!isFilled)
			 isFilled = true
			 this.value = value
			 lock.notifyAll
		}
	 }
  
	 def recv() = {
		lock.synchronized {
			 waitUntil (isFilled)
			 val temp = value
			 isFilled = false
			 lock.notifyAll
			 temp
		}
	 }
	 
}


class SimpleRunner(process:Process) {
  
	val p = process
 
	var links:HashMap[Link[_], LinkChannel] = null
 
	def start() {
		continue(new HashMap[Link[_], LinkChannel])
	}
 
	def continue(links:HashMap[Link[_], LinkChannel]) {
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
			case pp:Restriction => run(pp.instantiate)
			case pp:CompositionProcess => { // won't work in many cases
				new Thread() {
					override def run() { new SimpleRunner(pp.left).continue (links) }
				}.start
				new Thread() {
					override def run() { new SimpleRunner(pp.right).continue (links) }
				}.start
			}
			case pp:LinkProcess[_] => {
				if (!links.keySet.contains(pp.link)) {
					links += pp.link -> new LinkChannel(pp.link)
				}
				if (pp.action == Link.Action.Send) {
					links(pp.link).send(pp.name.value)
				} else {
					pp.asInstanceOf[LinkProcess[Any]].name := links(pp.link).recv
				}
			}
		}
	}

}
