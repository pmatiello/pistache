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
  
	val Q = new Process
	val R = new Process
	val S = new Process
  
	describe ("If") {
	  
		it ("should express a conditional execution of a process") {
			val P = Process(If (1 > 0) {Q}) 
			P match {
				case proc:IfProcess => proc.condition.apply must equal (true)
																proc.then must equal (Q)
			}
		}
  
		it ("should possible to express a conditional process as part of another process") {
			val P = Process(Q * If (1 > 0) {R} * S)
			
			val PC = P.asInstanceOf[ConcatenationProcess]
			PC.left.asInstanceOf[ConcatenationProcess].left must equal(Q)
			PC.left.asInstanceOf[ConcatenationProcess].right.asInstanceOf[IfProcess].condition.apply must equal (true)
			PC.left.asInstanceOf[ConcatenationProcess].right.asInstanceOf[IfProcess].then must equal (R)
			PC.right must equal (S)
		}
	}
		
	describe ("Else") {
	  
		it ("should express a conditional execution of a process") {
			val P = Process(If (1 > 0) {Q} Else {R})  
			P match {
				case proc:IfElseProcess =>
								proc.condition.apply must equal (true)
								proc.then must equal (Q)
								proc.elseThen must equal (R)
			}	
		}
  
		it ("should possible to express a conditional process as part of another process") {
			val P = Process(Q * (If (1 > 0) {R} Else {S}) * Q)
			
			val PC = P.asInstanceOf[ConcatenationProcess]
			PC.left.asInstanceOf[ConcatenationProcess].left must equal(Q)
			PC.left.asInstanceOf[ConcatenationProcess].right.asInstanceOf[IfElseProcess].condition.apply must equal (true)
			PC.left.asInstanceOf[ConcatenationProcess].right.asInstanceOf[IfElseProcess].then must equal (R)
			PC.left.asInstanceOf[ConcatenationProcess].right.asInstanceOf[IfElseProcess].elseThen must equal (S)
			PC.right must equal (Q)
		}
	  
	}

}
