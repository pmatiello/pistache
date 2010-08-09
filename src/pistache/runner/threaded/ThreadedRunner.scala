/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * A pi-Calculus specification runner using threads.
 */

package pistache.runner.threaded

import pistache.picalculus._

/** A local, multithreaded runner for pi-Calculus agents.
 * 
 *  @param agent the agent to be executed.
 *  @param parent the main runner in the computation 
 */
class ThreadedRunner (val agent:Agent) {
  
	private var threads = List[Thread]()
  
	/** Start the execution of the agent.
	 */
	def start {
		LinkStorage.initialize
  
		executeInNewThread(agent)
		
		waitAllThreads
	}
 
	/** Execute a given agent in a new thread.
	 *
	 *  @param agent the agent to be executed.
	 */
	private def executeInNewThread(agent:PiObject) {
		val thread = new Thread() {
			override def run() { execute(agent) }
		}

		thread.start
		
		synchronized {
			threads = thread :: threads
		}
	}
  
 	/** Wait for all registered threads to finish their execution.
	 */
	private def waitAllThreads() {
 		var active:List[Thread] = null
		while (true) {
			synchronized {
				if (threads.size > 0) {
					active = Nil
					threads.foreach { thread =>
						if (thread.isAlive) active = thread :: active
					}
					threads = active
				} else {
					return
				}
			}
			Thread.sleep(50 * threads.size)
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