/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A pi-Calculus specification runner using threads.
 */

package pistache.runner

import pistache.picalculus._
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

/** A centralized storage for stuff transmitted through links.
 */
private object LinkStorage {

	/** A concrete implementation of links.
	 */
	class LinkImplementation {
		private var isFilled = false
		private var value:Any = null
		private var lock:AnyRef = new Object
		
		/** Make the thread wait until the given condition is satisfied.
		 *
		 *  @param cond the condition 
		 */
		private def waitUntil(cond : => Boolean) {
			while (!cond) { lock.wait }
		}

		/** Send (store) a value through the link.
		 *
		 *  @param value the value 
		 */
		def send(value:Any) {
			lock.synchronized {
				waitUntil (!isFilled)
				isFilled = true
				this.value = value
				lock.notifyAll
			}
		}

		/** Receive (retrieve) a value through the link.
		 * 
		 *  @return a previously sent value 
		 */
		def recv:Any = {
			lock.synchronized {
				waitUntil (isFilled)
				val temp = value
				isFilled = false
				lock.notifyAll
				temp
			}
		}
	}
  
	var links:Map[Link[_], LinkImplementation] = null
  
	/** Initialize the storage. 
	 */
	def initialize() {
		links = new HashMap[Link[_], LinkImplementation]
	}

	/** Associate a link to an implementation, if it's not yet associated. 
	 */
	private def ready[T](link:Link[T]) {
		if (!links.keySet.contains(link)) {
			links += link -> new LinkImplementation
		}
	}

	/** Send the given name through the given link.
	 * 
	 *  @param link the link
	 *  @param name the name
	 */
	def send[T](link:Link[T], name:Name[T]) {
		ready(link)
		links(link).send(name.value)
	}

	/** Receive a value from the given link and store it on the given name.
	 * 
	 *  @param link the link
	 *  @param name the name
	 */
	def recv[T](link:Link[T], name:Name[T]) {
		ready(link)
		name := links(link).recv.asInstanceOf[T]
	}
  
}

/** A local, multithreaded runner for pi-Calculus processes.
 * 
 *  @param process the process to be executed.
 */
class ThreadedRunner(process:Process) {
  
	/** Start the execution of the process.
	 */
	def start = {
		LinkStorage.initialize
		run(process)
	}
  
	/** Run a given process.
	 *
	 *  @param process the process to be executed.
	 */
	def run(process:Process) {
		process match {
		  
			/* Execute action */
			case proc:Action => proc.procedure apply

			/* Execute processes sequentially */
			case proc:ConcatenationProcess => run(proc left)
											  run(proc right)
            
			/* Execute processes in parallel */
            case proc:CompositionProcess => {
              
            	val leftThread = new Thread() {
            		override def run() { new ThreadedRunner(null) run (proc.left) }
            	}               
            	val rightThread = new Thread() {
            		override def run() { new ThreadedRunner(null) run (proc.right) }
            	}
             
            	leftThread.start
            	rightThread.start
            	leftThread.join
            	rightThread.join
               
            }
            
            /* Execute processes conditionally */
			case proc:IfProcess => if (proc.condition apply) run(proc then)
			case proc:IfElseProcess => if (proc.condition apply) run(proc then) else run(proc elseThen)
   
			/* Execute processes with restricted names */
			case proc:Restriction => run(proc.process apply)
   
			/* Send and receive messages through links */
			case proc:LinkProcess[_] => {
				proc.action match {
					case Link.ActionType.Send => LinkStorage.send(proc.link, proc.name)
					case Link.ActionType.Receive => LinkStorage.recv(proc.link, proc.name)
				} 
			}
		}
	}
  
}
