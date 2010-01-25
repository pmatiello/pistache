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
  
	val Q = new FakeProcess
	val R = new FakeProcess
	val S = new FakeProcess
  
	describe ("If") {
	  
		it ("should express a conditional execution of a process") {
			val P = If (1 > 0) {Q}
			P match {
				case proc:IfProcess => proc.condition.apply must equal (true)
																proc.then must equal (Q)
			}
		}
  
		it ("should possible to express a conditional process as part of another process") {
			val P = Q * If (1 > 0) {R} * S
			
			P match {
				case pp:ConcatenationProcess => {
					pp.left match {
						case pl:ConcatenationProcess => {
							pl.left must equal (Q)
							pl.right match {
								case pi:IfProcess => pi.condition.apply must equal (true)
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
	  
		it ("should express a conditional execution of a process") {
			val P = If (1 > 0) {Q} Else {R}
			P match {
				case proc:IfElseProcess =>
								proc.condition.apply must equal (true)
								proc.then must equal (Q)
								proc.elseThen must equal (R)
			}	
		}
  
		it ("should possible to express a conditional process as part of another process") {
			val P = Q * (If (1 > 0) {R} Else {S}) * Q
			
			P match {
				case pp:ConcatenationProcess => {
					pp.left match {
						case pl:ConcatenationProcess => {
							pl.left must equal (Q)
							pl.right match {
								case pi:IfElseProcess => pi.condition.apply must equal (true)
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
