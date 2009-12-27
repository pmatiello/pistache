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
class IfSpec extends Spec with MustMatchers {
  
	describe ("If") {
	   	
		val Q = new Process {
			val description = null 
		}
		val R = new Process {
			val description = null
		}
		val S = new Process {
			val description = null
		}	  
	  
		it ("should be possible to express conditions") {
			val P = new Process {
				val description = If (1 > 0) {Q} 
			}
			P.description match {
				case proc:IfProcess => proc.condition.apply must equal (true)
																proc.description must equal (Q)
			}
		}
  
		it ("should possible to express conditions as part of a process") {
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

}
