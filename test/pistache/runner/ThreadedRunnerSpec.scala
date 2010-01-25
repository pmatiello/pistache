/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for ThreadedRunner.
 */

package pistache.runner

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
  
		it ("should run concatenation processes") {
			var execOrder = 1;
			var executed1 = 0
			val action1 = Action{executed1 = execOrder; execOrder += 1}
			var executed2 = 0
			val action2 = Action{executed2 = execOrder; execOrder += 1}
			var executed3 = 0
			val action3 = Action{executed3 = execOrder; execOrder += 1}
			val process = Process(action1*action2*action3)
			new ThreadedRunner(process).start
			executed1 must equal (1)
			executed2 must equal (2)
			executed3 must equal (3)
		}
  
		it ("should run parallel processes") {
			var executed1 = false
			val action1 = Action{executed1 = true}
			var executed2 = false
			val action2 = Action{executed2 = true}
			var executed3 = false
			val action3 = Action{executed3 = true}
			val process = Process(action1 | action2 | action3)
			new ThreadedRunner(process).start
			executed1 must be (true)
			executed2 must be (true)
			executed3 must be (true)
		}
  
		it ("should wait for the parallel process to finish before running the next outter concatenated process") {
			var execOrder = 1;
			var executed1 = 0
			val action1 = Action{Thread.sleep(100); executed1 = execOrder; execOrder += 1}
			var executed2 = 0
			val action2 = Action{Thread.sleep(200); executed2 = execOrder; execOrder += 1}
			var executed3 = 0
			val action3 = Action{executed3 = execOrder; execOrder += 1}
			val process = Process((action1|action2)*action3)
			new ThreadedRunner(process).start
			executed1 must equal (1)
			executed2 must equal (2)
			executed3 must equal (3)
		}
  
		it ("should run processes conditionally") {
			var executed1 = false
			var executed2 = false
			var action1 = Action(executed1 = true)
			var action2 = Action(executed2 = true)
			val process = Process(If (true) {action1} * If (false) {action2})
			new ThreadedRunner(process).start
			executed1 must be (true)
			executed2 must be (false)
		}
  
		it ("should run an alternative process branch conditionally") {
			var executedIf1 = false
			var executedElse1 = false
			var executedIf2 = false
			var executedElse2 = false

			var actionIf1 = Action(executedIf1 = true)
			var actionElse1 = Action(executedElse1 = true)
			var actionIf2 = Action(executedIf2 = true)
			var actionElse2 = Action(executedElse2 = true)
   
			val process = Process((If (true) {actionIf1} Else {actionElse1}) * (If (false) {actionIf2} Else {actionElse2}))
			new ThreadedRunner(process).start
			
			executedIf1 must be (true)
			executedElse1 must be (false)
			executedIf2 must be (false)
			executedElse2 must be (true)
		}
  
		it ("should run processes with restricted names") {
			var hasRan = false;
			var finalValue = 0;
			
			lazy val process:Process = Process{
				var local = 0;
				val action = Action{
				  local = local+1
				  finalValue = local
				  hasRan = true
				}
				
				Process(If (!hasRan) {action * process} Else {action})
			}
   
			new ThreadedRunner(process).start
			finalValue must equal (1)
		}
  
		it ("should send and receive messages") {
			val name2send = Name(true)
			val name2recv = Name(false)
			val link = Link[Boolean]
			
			val process = Process(link~name2send*link(name2recv))
   
			new ThreadedRunner(process).start
   
			name2send.value must equal (name2recv.value)
		}
  
	}

}
