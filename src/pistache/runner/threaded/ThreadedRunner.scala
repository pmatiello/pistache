/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A pi-Calculus specification runner using threads.
 */

package pistache.runner.threaded

import java.util.concurrent.ExecutorService
import pistache.picalculus._
import java.util.concurrent.Executors

/** A local, multithreaded runner for pi-Calculus agents.
 * 
 *  @param agent the agent to be executed.
 */
class ThreadedRunner (val agent:Agent) {
  
	private var executor:ExecutorService = null
	private var threadCount = 0
  
	/** Start the execution of the agent.
	 */
	def start {
		LinkStorage.initialize
		executor = Executors.newCachedThreadPool
  
		executeInNewThread(agent)

		waitAllThreads
		executor.shutdown
	}
 
	/** Execute a given agent in a new thread.
	 *
	 *  @param agent the agent to be executed.
	 */
	private def executeInNewThread(agent:PiObject) {
		
		increaseThreadCount()
		
		val runnable = new Runnable() {
			override def run() { execute(agent); decreaseThreadCount() }
		}

		executor.execute(runnable)
	}
  
 	/** Wait for all registered threads to finish their execution.
	 */
	private def waitAllThreads() {
		synchronized {
			while (threadCount != 0) wait;
		}
	}
	
	private def increaseThreadCount() {
		synchronized {
			threadCount += 1
			notify
		}
	}
	
	private def decreaseThreadCount() {
		synchronized {
			threadCount -= 1
			notify
		}
	}
  
	/** Execute a given agent.
	 *
	 *  @param agent the agent to be executed.
	 */
	private def execute(agent:PiObject) {
		agent match {
		  
			/* Ignore NilAgent */
			case NilAgent() => {}

			/* Execute (restricted) agents */
			case RestrictedAgent(agent) => execute(agent apply)
		  
			/* Execute action */
			case ActionPrefix(procedure) => procedure apply

			/* Execute prefixes sequentially */
			case ConcatenationPrefix(left, right) => execute(left apply)
											  		 execute(right apply)
     
			/* Execute prefix -- agent sequentially */
			case ConcatenationAgent(left, right) =>	execute(left apply)
											  		execute(right apply)
            
			/* Execute guard-prefix -- agent sequentially */
			case GuardedAgent(left, right) => execute(left apply)
											  execute(right apply)

            
			/* Execute agents in parallel */
            case CompositionAgent(left, right) => executeInNewThread(left apply)
            									  executeInNewThread(right apply)
                         
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
            	execute(continue)
            }
            
            /* Execute agents conditionally */
			case MatchAgent(condition, then) => if (condition apply) execute(then apply)
			
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