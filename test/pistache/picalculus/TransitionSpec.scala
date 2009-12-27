/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Transition class.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class TransitionSpec extends Spec with MustMatchers {

	 describe ("Transition") {
	   
		it ("should wrap a closure") {
			var executed = 0;
			val transition = Transition(
				executed = executed + 1
			)
			executed must be (0)
			transition.run
			executed must be (1)
		}
  
		it ("should be a process") {
			val process:Process = Transition(
				fail()	// must not be run
			)
			assert(true)
		}
	   
	 }
  
}
