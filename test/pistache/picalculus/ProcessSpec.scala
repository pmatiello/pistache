/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Process trait and companion classes.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class ProcessSpec extends Spec {
  
	object R extends Process {
		val description = null 
	}
	object S extends Process {
		val description = null
	}
	object T extends Process {
		val description = null
	}
  
	describe ("Process") {
  
		it ("should be written as a concatenation of Processes") {
			object P extends Process {
				val description = R :: S :: T
			}
			
			P.description match {
			  case ConcatenationProcess(R, ConcatenationProcess(S, T)) => assert(true)
			  case _ => assert(false)
			}
		}
  
		it ("should be self-embeddable") {
			object P extends Process {
				val description = R :: this
			}
 
			P.description match {
			  case ConcatenationProcess(R, P) => assert(true)
			  case _ => assert(false)
			}
		}
  
		it ("should have a sum operator") {
			object Q extends Process {
				val description = R + S
			}
			object P extends Process {
				val ap = mock(classOf[Process])
				val description = ap :: Q + R + S
			}
			
			Q.description match {
			  case SumProcess(R, S) => assert(true)
			  case _ => assert(false)
			}
			
			P.description match {
			  case ConcatenationProcess(ap, SumProcess(SumProcess(Q, R), S)) => assert(true)
			  case _ => assert(false)
			}
		}
	  
	}

}
