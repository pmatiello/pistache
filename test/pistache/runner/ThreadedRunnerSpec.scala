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
			val transition = Action(executed = true)
			new ThreadedRunner(transition).start
			executed must equal (true)
		}
  
		it ("should run concatenation processes") {
			var execOrder = 1;
			var executed1 = 0
			val transition1 = Action{executed1 = execOrder; execOrder += 1}
			var executed2 = 0
			val transition2 = Action{executed2 = execOrder; execOrder += 1}
			var executed3 = 0
			val transition3 = Action{executed3 = execOrder; execOrder += 1}
			val process = Process(transition1*transition2*transition3)
			new ThreadedRunner(process).start
			executed1 must equal (1)
			executed2 must equal (2)
			executed3 must equal (3)
		}
  
		it ("should run parallel processes") {
			var executed1 = false
			val transition1 = Action{executed1 = true}
			var executed2 = false
			val transition2 = Action{executed2 = true}
			var executed3 = false
			val transition3 = Action{executed3 = true}
			val process = Process(transition1 | transition2 | transition3)
			new ThreadedRunner(process).start
			executed1 must equal (true)
			executed2 must equal (true)
			executed3 must equal (true)
		}
  
		it ("should wait for the parallel process to finish before running the next outter concatenated process") {
			var execOrder = 1;
			var executed1 = 0
			val transition1 = Action{Thread.sleep(100); executed1 = execOrder; execOrder += 1}
			var executed2 = 0
			val transition2 = Action{Thread.sleep(200); executed2 = execOrder; execOrder += 1}
			var executed3 = 0
			val transition3 = Action{executed3 = execOrder; execOrder += 1}
			val process = Process((transition1|transition2)*transition3)
			new ThreadedRunner(process).start
			executed1 must equal (1)
			executed2 must equal (2)
			executed3 must equal (3)
		}
  
	}

}
