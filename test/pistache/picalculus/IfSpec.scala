/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for If object.
 */

package pistache.picalculus

import pistache.testing._
import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class IfSpec extends Spec with MustMatchers {
  
	val Q = new FakeAgent
	val R = new FakeAgent
	val S = new FakePrefix
  
	describe ("If") {
	  
		it ("should express a conditional execution of a agent") {
			val P = If (1 > 0) {Q}
			P match {
				case IfAgent(condition, then) =>	condition.apply must equal (true)
													then.apply must equal (Q)
			}
		}
  
		it ("should possible to express a conditional agent as part of another agent") {
			val P = S * If (1 > 0) {Q} * R
			
			P match {
				case ConcatenationAgent(left, right) => {
					left.apply match {
						case ConcatenationAgent(left, right) => {
							left.apply must equal (S)
							right.apply match {
								case IfAgent(condition, then) =>	condition.apply must equal (true)
																	then.apply must equal (Q)
							}
						}
					}
					right.apply must equal (R)
				}
			}
   		}
	}

}
