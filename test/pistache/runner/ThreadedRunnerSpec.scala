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
		
		it ("should run transitions") {
			var executed = false
			val transition = Transition(executed = true)
			new ThreadedRunner(transition).start
			executed must equal (true)
		}
  
		it ("should run concatenated transitions") {
			var executed1 = 0
			val transition1 = Transition(executed1 = 1)
			var executed2 = 1
			val transition2 = Transition(executed2 = executed1+1)
			var executed3 = 2
			val transition3 = Transition(executed3 = executed2+1)
			val process = Process(transition1*transition2*transition3)
			new ThreadedRunner(process).start
			executed1 must equal (1)
			executed2 must equal (2)
			executed3 must equal (3)
		}
  
	}

}
