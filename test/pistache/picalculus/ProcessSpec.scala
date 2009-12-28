/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Process trait and companion classes.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class ProcessSpec extends Spec with MustMatchers {
  
	val Q = new Process
	val R = new Process
	val S = new Process
	
	describe ("Process") {
  
		it ("should be written as a concatenation of Processes") {
			val P = Process(Q * R * S)
			P.asInstanceOf[ConcatenationProcess].left.asInstanceOf[ConcatenationProcess].left must equal (Q)
			P.asInstanceOf[ConcatenationProcess].left.asInstanceOf[ConcatenationProcess].right must equal (R)
			P.asInstanceOf[ConcatenationProcess].right must equal (S)
		}
  
		it ("should be self-embeddable") {
			lazy val P:Process = Process(R*P)
			P.asInstanceOf[ConcatenationProcess].left must equal (R)
			P.asInstanceOf[ConcatenationProcess].right must equal (P)
		}
  
		it ("should allow cyclic references") {
			lazy val P:Process = Process(R*Q)
			lazy val Q:Process = Process(P)
			P.asInstanceOf[ConcatenationProcess].left must equal(R)
			P.asInstanceOf[ConcatenationProcess].right must equal(Q)
		}
  
		it ("should have a sum operator") {
			val P = Process(Q + R + S)

			P.asInstanceOf[SumProcess].left.asInstanceOf[SumProcess].left must equal (Q)
			P.asInstanceOf[SumProcess].left.asInstanceOf[SumProcess].right must equal (R)
			P.asInstanceOf[SumProcess].right must equal (S)
		}
  
		it ("should be so that concatenation has precedence over sum") {
			val P:Process = Process(Q*R + Q*S)

			val PS = P.asInstanceOf[SumProcess]
			PS.left.asInstanceOf[ConcatenationProcess].left must equal (Q)
			PS.left.asInstanceOf[ConcatenationProcess].right must equal (R)
			PS.right.asInstanceOf[ConcatenationProcess].left must equal (Q)
			PS.right.asInstanceOf[ConcatenationProcess].right must equal (S)
		}
  
  		it ("should have a composition operator") {
			val P = Process(Q | R | S)

			P.asInstanceOf[CompositionProcess].left.asInstanceOf[CompositionProcess].left must equal (Q)
			P.asInstanceOf[CompositionProcess].left.asInstanceOf[CompositionProcess].right must equal (R)
			P.asInstanceOf[CompositionProcess].right must equal (S)
		}
  
		it ("should be so that concatenation has precedence over composition") {
			val P:Process = Process(Q*R | Q*S)
			
			val PC = P.asInstanceOf[CompositionProcess]
			PC.left.asInstanceOf[ConcatenationProcess].left must equal (Q)
			PC.left.asInstanceOf[ConcatenationProcess].right must equal (R)
			PC.right.asInstanceOf[ConcatenationProcess].left must equal (Q)
			PC.right.asInstanceOf[ConcatenationProcess].right must equal (S)
		}
	  
	}

}
