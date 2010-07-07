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
		private var writer:AnyRef = null
		private var reader:AnyRef = null
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
				waitUntil(isEmpty && writer == null)
				put(value)
			}
		}
  
		/** Send (store) a value through the link.
		 *  
		 *  This function will try only once.
		 *
		 *  @param value the value
		 *  @return whether the sending was successful
		 */
		def attemptSend(value:Any) = {
			lock.synchronized {
				if (writer == null) {
					writer = Thread.currentThread
				}
				if (isEmpty && writer == Thread.currentThread && reader != null) {
					put(value)
					writer = null
					true
				} else {
			    	false
			    }
			}
		}

		/** Send (store) a value through the link, unconditionally.
		 *
		 *  @param value the value 
		 */
		private def put(value:Any) {
			buffer = value
			isEmpty = false
			writer = Thread.currentThread
			lock.notifyAll
			waitUntil(isEmpty)
			writer = null
			lock.notifyAll
		} 

		/** Receive (retrieve) a value through the link.
		 * 
		 *  @return a previously sent value 
		 */
		def recv:Any = {
			lock.synchronized {
				reader = Thread.currentThread
				waitUntil (!isEmpty)
				reader = null
				get
			}
		}
  
		/** Receive (retrieve) a value through the link.
		 *
   		 *  This function will try only once.
		 * 
		 *  @return Tuple containing: (success status, a previously sent value) 
		 */
		def attemptRecv = {
			lock.synchronized {
				if (!isEmpty) {
					(true, get)
				} else if (writer != null && writer != Thread.currentThread) {
					(true, recv)
				} else {
					(false, null)
				}
			}
		}
  
		/** Receive (retrieve) a value through the link, unconditionally.
		 * 
		 *  @return a previously sent value 
		 */
		private def get:Any = {
			val temp = buffer
			isEmpty = true
			lock.notifyAll
			temp
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
		links(link).send(if (name != null) name.value else null)
	}
 
	/** Send the given name through the given link.
	 * 
	 *  This function will try only once.
	 *
	 *  @param link the link
	 *  @param name the name
	 * 
	 *  @return whether the sending was successful
	 */
	def attemptSend[T](link:Link[T], name:Name[T]) = {
		ready(link)
		links(link).attemptSend(if (name != null) name.value else null)
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
 
	/** Receive a value from the given link and store it on the given name.
	 *
	 *  This function will try only once.
	 *
	 *  @param link the link
	 *  @param name the name
	 * 
	 *  @return whether the sending was successful
	 */
	def attemptRecv[T](link:Link[T], name:Name[T]):Boolean = {
		ready(link)
		val (success, value) = links(link).attemptRecv 
		name := value.asInstanceOf[T]
		success
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
	private def run(agent:PiObject) {
		agent match {
		  
			/* Ignore NilAgent */
			case NilAgent() => {}

			/* Execute (restricted) agents */
			case RestrictedAgent(agent) => run(agent apply)
		  
			/* Execute action */
			case ActionPrefix(procedure) => procedure apply

			/* Execute prefixes sequentially */
			case ConcatenationPrefix(left, right) => run(left apply)
											  		 run(right apply)
     
			/* Execute prefix -- agent sequentially */
			case ConcatenationAgent(left, right) =>	run(left apply)
											  		run(right apply)
            
			/* Execute guard-prefix -- agent sequentially */
			case GuardedAgent(left, right) => run(left apply)
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
            
			/* Execute one of many agents */
            case SummationAgent(left, right) => {
            	val agents = sumTerms(left apply) ::: sumTerms(right apply)
            	var done = false
            	var continue:Agent = null
            	while (!done) {
            		agents.foreach { agent =>
	            		if (!done) {
	            			agent.left.apply match {
		            			case ActionPrefix(procedure) =>
		            				procedure.apply
		            				done = true
		            				continue = agent.right.apply
		            				
		            			case LinkPrefix(link, Link.ActionType.Send, name) =>
		            				done = LinkStorage.attemptSend(link, name)
		            				if (done) { continue = agent.right.apply }
		            				
		            			case LinkPrefix(link, Link.ActionType.Receive, name) =>
		            			  	done = LinkStorage.attemptRecv(link, name)
		            				if (done) { continue = agent.right.apply }
	            			}
	            		}
            		}
            	}
            	run(continue)
            }
            
            /* Execute agents conditionally */
			case MatchAgent(condition, then) => if (condition apply) run(then apply)
			
			/* Send and receive messages through links */
			case LinkPrefix(link, Link.ActionType.Send, name) => LinkStorage.send(link, name)
			case LinkPrefix(link, Link.ActionType.Receive, name) => LinkStorage.recv(link, name)
			
		}
  
	}
 
	/* Build a list of all agents in a summation.
	 */
	private def sumTerms(agent:Agent):List[GuardedAgent] = {
		agent match {
			case agent:GuardedAgent =>	agent :: Nil
			case SummationAgent(left, right) =>	sumTerms(left apply) ::: sumTerms(right apply)
		}
	}
  
}