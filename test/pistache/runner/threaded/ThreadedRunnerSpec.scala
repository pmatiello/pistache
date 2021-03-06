/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for ThreadedRunner.
 */

package pistache.runner.threaded

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import pistache.picalculus._

@RunWith(classOf[JUnitRunner])
class ThreadedRunnerSpec extends Spec with MustMatchers {
  
	describe ("ThreadedRunner") {
		
		it ("should run actions") {
			var executed = false
			val action = Action(executed = true)
			new ThreadedRunner(action).start
			executed must be (true)
		}
  
		it ("should run concatenation agents") {
			var execOrder = 1;
			var executed1 = 0
			val action1 = Action{executed1 = execOrder; execOrder += 1}
			var executed2 = 0
			val action2 = Action{executed2 = execOrder; execOrder += 1}
			var executed3 = 0
			val action3 = Action{executed3 = execOrder; execOrder += 1}
			val agent = Agent(action1*action2*action3)
			new ThreadedRunner(agent).start
			executed1 must equal (1)
			executed2 must equal (2)
			executed3 must equal (3)
		}
  
		it ("should execute guarded agents") {
			var execOrder = 1;
			var executed1 = 0
			val action1 = Action{executed1 = execOrder; execOrder += 1}
			var executed2 = 0
			val action2 = Action{executed2 = execOrder; execOrder += 1}
			var executed3 = 0
			val action3 = Action{executed3 = execOrder; execOrder += 1}
			val agent = Agent(action1::action2::action3)
			new ThreadedRunner(agent).start
			executed1 must equal (1)
			executed2 must equal (2)
			executed3 must equal (3)
		}
  
		it ("should run parallel agents") {
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			var executed3 = false
			val action3 = Action{executed3 = true}
			val agent = Agent(action1 | action2 | action3)
			new ThreadedRunner(agent).start
			executed1 must be (true)
			executed2 must be (true)
			executed3 must be (true)
		}
  
		it ("should run summation agents guarded by silent actions") {
			var executed1 = 0
			val action1 = Action{executed1 = 1}
			var executed2 = 0
			val action2 = Action{executed2 = 1}
			var executed3 = 0
			val action3 = Action{executed3 = 1}
			var executedAgent = false
			val actionAgent = Action{executedAgent = true}
			val agent = Agent((action1 :: actionAgent) + (action2 :: actionAgent) + (action3 :: actionAgent))
			new ThreadedRunner(agent).start
			(executed1 + executed2 + executed3) must equal (1)
			executedAgent must be (true)
		}
  
		it ("should run summation agents guarded by input prefixes") {
			val link1 = Link[Any]
			val link2 = Link[Any]
			val link3 = Link[Any]
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			var executed3 = false
			val action3 = Action{executed3 = true}
			val agent = Agent((link1() :: action1) + (link2() :: action2) + (link3() :: action3))
			new ThreadedRunner(agent | link3~()).start
			executed1 must be (false)
			executed2 must be (false)
			executed3 must be (true)
		}
  
		it ("should run summation agents guarded by output prefixes") {
			val link1 = Link[Any]
			val link2 = Link[Any]
			val link3 = Link[Any]
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			var executed3 = false
			val action3 = Action{executed3 = true}
			val agent = Agent((link1~() :: action1) + (link2~() :: action2) + (link3~() :: action3))
			new ThreadedRunner(agent | link3()).start
			executed1 must be (false)
			executed2 must be (false)
			executed3 must be (true)
		}
  
		it ("should not perform communication between two agents in a sum") {
			val link1 = Link[Any]
			val link2 = Link[Any]
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			var executed3 = false
			val action3 = Action{executed3 = true}
			val wait = Action { Thread.sleep(100) }
			val agent = Agent((link1~() :: action1) + (link1() :: action2) + (link2~() :: action3))
			new ThreadedRunner(agent | wait*link2()).start
			executed1 must be (false)
			executed2 must be (false)
			executed3 must be (true)
		}
  
  
		it ("should perform communication between two agents in a different sums") {
			val link1 = Link[Any]
			val link2 = Link[Any]
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			val sum1 = Agent((link1~() :: action1) + (link2~() :: action2))
			val sum2 = Agent((link1() :: action1) + (link2() :: action2))
			new ThreadedRunner(sum1 | sum2).start
		}
  
		it ("should not pick more than one agent in a sum (output guarded)") {
			val link = Link[Any]
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			val wait = Action { Thread.sleep(100) }
			val agent = Agent((link~() :: wait * link~() * action1) + (link~() :: wait * link~() * action2))
			new ThreadedRunner(agent | link()*link()).start
			(executed1 && executed2) must be (false)
			(executed1 || executed2) must be (true)
		}
  
		it ("should not pick more than one agent in a sum (input guarded)") {
			val link = Link[Any]
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			val wait = Action { Thread.sleep(100) }
			val agent = Agent((link() :: wait * link() * action1) + (link() :: wait * link() * action2))
			new ThreadedRunner(agent | link~()*link~()).start
			(executed1 && executed2) must be (false)
			(executed1 || executed2) must be (true)
		}


		it ("should not mislead agents into reading from channels that won't be written") {
			val link1 = Link[Any]
			val link2 = Link[Any]
			val wait = Action { Thread.sleep(100) }
			val agent1 = Agent((link1~() :: link2~() ) + (link2~() :: link1~()))
			val agent2 = Agent((link1() :: NilAgent()) + (link2() :: NilAgent()))
			new ThreadedRunner(agent1 | wait*agent2 | wait*agent2).start
		}
  
		it ("should not mislead agents into not writting to channels that can be written") {
			val link1 = Link[Any]
			val link2 = Link[Any]
			val wait = Action { Thread.sleep(100) }
			val agent1 = Agent((link1() :: link2() ) + (link2() :: link1()))
			val agent2 = Agent((link1~() :: NilAgent()) + (link2~() :: NilAgent()))
			new ThreadedRunner(agent1 | wait*agent2 | wait*agent2).start
		}
  
		it ("should run agents conditionally") {
			var executed1 = false
			var executed2 = false
			var action1 = Action(executed1 = true)
			var action2 = Action(executed2 = true)
			val agent = Agent(If (true) {action1} | If (false) {action2})
			new ThreadedRunner(agent).start
			executed1 must be (true)
			executed2 must be (false)
		}
  
		it ("should run agents with restricted names") {
			var hasRan = false;
			var finalValue = 0;
			
			lazy val agent:Agent = Agent{
				var local = 0;
				val action = Action{
				  local = local+1
				  finalValue = local
				  hasRan = true
				}
				
				Agent(action * If (!hasRan) {agent})
			}
   
			new ThreadedRunner(agent).start
			finalValue must equal (1)
		}
  
		it ("should send and receive messages") {
			val name2send = Name(true)
			val name2recv = Name(false)
			val link = Link[Boolean]
			
			val agent = Agent(link~name2send | link(name2recv))
   
			new ThreadedRunner(agent).start
   
			name2send.value must equal (name2recv.value)
		}
  
  		it ("should send and receive empty messages") {
			val link = Link[Any]
			
			val agent = Agent(link~() | link())
   
			new ThreadedRunner(agent).start   
		}
		
		it ("should work with agents with arguments") {
			// See: Milner, R., Parrow, J., and Walker, D. 1992. A calculus of mobile processes, Part I, Chapter 4, example 5
			val send = Link[Int]
			val recv = Link[Int]
			val value = Name(5)
			def Exec(get:Link[Int], put:Link[Int]) = Agent{
				val x = Name[Int]
				val action = Action{x := x.value+1}
				get(x)*action*put~x
			}
			val agent = Agent(send~value*recv(value) | Exec(send, recv))
			
			new ThreadedRunner(agent).start
			
			value.value must equal (6)
		}
  
		it ("should work with agents with arguments conditionally") {
			var exec1 = Name(false)
			var exec2 = Name(false)
			
			def Exec(name:Name[Boolean]) = Agent{
				val action = Action{name := true}
				action
			}
   
			val agent = Agent(If (true) {Exec(exec1)} | If (false) {Exec(exec2)})
			
			new ThreadedRunner(agent).start
			
			exec1.value must equal (true)
			exec2.value must equal (false)
		}
  
	}

}
