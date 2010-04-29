/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Action class.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ActionSpec extends Spec with MustMatchers {

	 describe ("Action") {
	   
		it ("should wrap a closure") {
			var executed = 0;
			val transition = Action(
				executed = executed + 1
			)
			executed must equal (0)
			transition.procedure.apply
			executed must equal (1)
		}
  
		it ("should be a prefix") {
			val prefix:Prefix = Action(
				fail()	// must not be run
			)
			assert(true)
		}
  
		it ("should be a agent") {
			val agent:Agent = Action(
				fail()	// must not be run
			)
			assert(true)
		}
	   
	 }
  
}
