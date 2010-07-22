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
		private var empty = true
		private var blocked = false
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
				waitUntil(empty && (writer == null || writer == Thread.currentThread))
				writer = Thread.currentThread
				buffer = value
				empty = false
				lock.notifyAll
				waitUntil(empty)
				writer = null
				blocked = false
				lock.notifyAll
			}
		}
  
		/** Send (store) a value through the link.
		 *  
		 *  This function will try only once.
		 *
		 *  @param value the value
		 *  @return whether the sending was successful
		 */
		def guardedSend(value:Any) = {
			lock.synchronized {
				if (writer == null || empty) {
					writer = Thread.currentThread
				}
				if (empty && writer == Thread.currentThread && (reader != null && reader != Thread.currentThread)) {
					send(value)
					true
				} else {
			    	false
			    }
			}
		}

		/** Receive (retrieve) a value through the link.
		 * 
		 *  @return a previously sent value 
		 */
		def recv:Any = {
			lock.synchronized {
				reader = Thread.currentThread
				waitUntil (!empty)
				val temp = buffer
				empty = true
				reader = null
				blocked = true
				lock.notifyAll
				temp
			}
		}
  
		/** Receive (retrieve) a value through the link.
		 *
   		 *  This function will try only once.
		 * 
		 *  @return Tuple containing: (success status, a previously sent value) 
		 */
		def guardedRecv = {
			lock.synchronized {
				if (!blocked && writer != null && writer != Thread.currentThread) {
					if (!empty) {
						(true, recv)  
					} else {
						reader = Thread.currentThread
						(false, null)
					}
				} else {
					(false, null)
				}
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
	def guardedSend[T](link:Link[T], name:Name[T]) = {
		ready(link)
		links(link).guardedSend(if (name != null) name.value else null)
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
	def guardedRecv[T](link:Link[T], name:Name[T]):Boolean = {
		ready(link)
		val (success, value) = links(link).guardedRecv 
		name := value.asInstanceOf[T]
		success
	}
  
}

/** A local, multithreaded runner for pi-Calculus agents.
 * 
 *  @param agent the agent to be executed.
 *  @param parent the main runner in the computation 
 */
class ThreadedRunner private (val agent:Agent, val parent:ThreadedRunner) {
  
	private var threadList = List[Thread]()

	/** Public constructor for this class.
	 * 
	 *  @param agent the agent to be executed.
	 */
	def this(agent:Agent) = this(agent, null)
  
	/** Start the execution of the agent.
	 */
	def start {
		LinkStorage.initialize
		run(agent)
		waitAllThreads
	}

 	/** Continue the execution of agent on a new thread.
 	 *  
     *  @param parentRunner The first runner instatiated in this execution.
	 */
	private def continue {
		run(agent)
	}
 
 	/** The parent runner for this computation.
	 */
	private def parentRunner = {
		if (parent != null) parent else this
	}
 
 	/** Register a thread running an instance of this class.
 	 *  
     *  @param thread The thread.
	 */
	private def waitThread(thread:Thread) {
		synchronized {
			threadList = thread :: threadList
		}
	}
 
 	/** Wait for all registered threads to finish their execution.
	 */
	private def waitAllThreads() {
		var thread:Thread = null
		while (true) {
			synchronized {
				if (threadList.size > 0) {
					thread = threadList.head
					threadList = threadList.tail
				} else {
					return
				}
			}
			thread.join
		}
	}
  
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
            		override def run() { new ThreadedRunner(left apply, parentRunner) continue }
            	}               
            	val rightThread = new Thread() {
            		override def run() { new ThreadedRunner(right apply, parentRunner) continue }
            	}
             
            	leftThread.start
            	rightThread.start
             
            	parentRunner.waitThread(leftThread)
            	parentRunner.waitThread(rightThread)               
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
		            				done = LinkStorage.guardedSend(link, name)
		            				if (done) { continue = agent.right.apply }
		            				
		            			case LinkPrefix(link, Link.ActionType.Receive, name) =>
		            			  	done = LinkStorage.guardedRecv(link, name)
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