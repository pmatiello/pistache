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
			new SimpleRunner(transition).start
			executed must equal (true)
		}
	}

}
