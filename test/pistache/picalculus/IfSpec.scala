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
	val S = new FakeAgent
  
	describe ("If") {
	  
		it ("should express a conditional execution of a agent") {
			val P = If (1 > 0) {Q}
			P match {
				case proc:IfAgent => proc.condition.apply must equal (true)
				proc.then must equal (Q)
			}
		}
  
		it ("should possible to express a conditional agent as part of another agent") {
			val P = Q * If (1 > 0) {R} * S
			
			P match {
				case pp:ConcatenationAgent => {
					pp.left match {
						case pl:ConcatenationAgent => {
							pl.left must equal (Q)
							pl.right match {
								case pi:IfAgent =>	pi.condition.apply must equal (true)
													pi.then must equal (R)
							}
						}
					}
					pp.right must equal (S)
				}
			}
   		}
	}
		
	describe ("Else") {
	  
		it ("should express a conditional execution of a agent") {
			val P = If (1 > 0) {Q} Else {R}
			P match {
				case proc:IfElseAgent =>
								proc.condition.apply must equal (true)
								proc.then must equal (Q)
								proc.elseThen must equal (R)
			}	
		}
  
		it ("should possible to express a conditional agent as part of another agent") {
			val P = Q * (If (1 > 0) {R} Else {S}) * Q
			
			P match {
				case pp:ConcatenationAgent => {
					pp.left match {
						case pl:ConcatenationAgent => {
							pl.left must equal (Q)
							pl.right match {
								case pi:IfElseAgent =>	pi.condition.apply must equal (true)
														pi.then must equal (R)
														 pi.elseThen must equal (S)
							}
						}
					}
					pp.right must equal (Q)
				}
			}
		}
	  
	}

}
