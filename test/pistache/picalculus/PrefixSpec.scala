/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Agent trait and companion classes.
 */

package pistache.picalculus

import pistache.testing._
import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class PrefixSpec extends Spec with MustMatchers {
  
	val Q = new FakePrefix
	val R = new FakePrefix
 	val S = new FakePrefix
	
	describe ("Prefix") {
  
		it ("should be written as a concatenation of prefixes") {
			val P = Q * R * S
			P match {
				case ConcatenationPrefix(left, right) => {
					left.apply match {
						case ConcatenationPrefix(left, right) =>	left.apply must equal (Q)
																	right.apply must equal (R)
					}
					right.apply must equal (S)
				}
			}
		}
  
		it ("should be converted implicitly to Agent") {
			val P:Agent = Q
			P match {
				case ConcatenationAgent(agent, NilAgent)	=> agent.apply must equal (Q)
			}
		}
  
	}
}
