/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for If object.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class IfSpec extends Spec with MustMatchers {
  
	val Q = new Process {
		val description = null 
	}
	val R = new Process {
		val description = null
		}
	val S = new Process {
		val description = null
	}
  
	describe ("If") {
	  
		it ("should express a conditional execution of a process") {
			val P = new Process {
				val description = If (1 > 0) {Q} 
			}
			P.description match {
				case proc:IfProcess => proc.condition.apply must equal (true)
																proc.description must equal (Q)
			}
		}
  
		it ("should possible to express a conditional process as part of another process") {
			val P = new Process {
				val description = Q * If (1 > 0) {R+S} * S
			}
			P.description match {
				case ConcatenationProcess(ConcatenationProcess(Q, proc:IfProcess), S) =>
				  										proc.condition.apply must equal (true)
														proc.description must equal (SumProcess(R, S))
				case _ => fail()
			}
		}
	}
		
	describe ("Else") {
	  
		it ("should express a conditional execution of a process") {
			val P = new Process {
				val description = If (1 > 0) {Q} Else {R}  
			}
			P.description match {
				case proc:IfElseProcess =>
								proc.condition.apply must equal (true)
								proc.description must equal (Q)
								proc.descriptionElse must equal (R)
			}	
		}
  
		it ("should possible to express a conditional process as part of another process") {
			val P = new Process {
				val description = Q * (If (1 > 0) {R} Else {S}) * Q
			}
			P.description match {
				case ConcatenationProcess(ConcatenationProcess(Q, proc:IfElseProcess), Q) =>
				  								proc.condition.apply must equal (true)
												proc.description must equal (R)
				  								proc.descriptionElse must equal (S)
				case _ => fail()
			}
		}
	  
	}

}
