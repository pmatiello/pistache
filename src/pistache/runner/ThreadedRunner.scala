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
		private var buffer:Any = null
		private var isEmpty = true
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
				waitUntil(isEmpty)
				buffer = value
				isEmpty = false
				lock.notifyAll
				waitUntil(isEmpty)
			}
		}

		/** Receive (retrieve) a value through the link.
		 * 
		 *  @return a previously sent value 
		 */
		def recv:Any = {
			lock.synchronized {
				waitUntil (!isEmpty)
				val temp = buffer
				isEmpty = true
				lock.notifyAll
				temp
			}
		}
	}
  
	private var links:Map[Link[_], LinkImplementation] = null
	private val lock:AnyRef = new Object
  
	/** Initialize the storage. 
	 */
	def initialize() {
		links = new HashMap[Link[_], LinkImplementation]
	}

	/** Associate a link to an implementation, if it's not yet associated. 
	 */
	private def ready[T](link:Link[T]) {
		lock.synchronized {
			if (!links.keySet.contains(link)) {
				links += link -> new LinkImplementation
			}
			lock.notifyAll
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

/** A local, multithreaded runner for pi-Calculus agents.
 * 
 *  @param agent the agent to be executed.
 */
class ThreadedRunner(val agent:Agent) {
  
	/** Start the execution of the agent.
	 */
	def start = {
		LinkStorage.initialize
		run(agent)
	}
 
	/** Run the agent.
	 */
	private def run { run(agent) }
  
	/** Run a given agent.
	 *
	 *  @param agent the agent to be executed.
	 */
	private def run(agent:Agent) {
		agent match {

			/* Execute (restricted) agents */
			case RestrictedAgent(agent) => run(agent apply)
		  
			/* Execute action */
			case ActionAgent(procedure) => procedure apply

			/* Execute agents sequentially */
			case ConcatenationAgent(left, right) => run(left apply)
											  		run(right apply)
            
			/* Execute agents in parallel */
            case CompositionAgent(left, right) => {
              
            	val leftThread = new Thread() {
            		override def run() { new ThreadedRunner(left apply) run }
            	}               
            	val rightThread = new Thread() {
            		override def run() { new ThreadedRunner(right apply) run }
            	}
             
            	leftThread.start
            	rightThread.start
            	leftThread.join
            	rightThread.join
               
            }
            
            /* Execute agents conditionally */
			case IfAgent(condition, then) => if (condition apply) run(then apply)
			
			/* Send and receive messages through links */
			case LinkAgent(link, action, name) => {
				action match {
					case Link.ActionType.Send => LinkStorage.send(link, name)
					case Link.ActionType.Receive => LinkStorage.recv(link, name)
				} 
			}
		}
	}
  
}
