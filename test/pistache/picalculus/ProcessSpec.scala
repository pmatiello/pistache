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
  
	object Q extends Process {
		val description = null 
	}
	object R extends Process {
		val description = null
	}
	object S extends Process {
		val description = null
	}
  
	describe ("Process") {
  
		it ("should be written as a concatenation of Processes") {
			object P extends Process {
				val description = Q * R * S
			}
			
			P.description match {
			  case ConcatenationProcess(ConcatenationProcess(Q, R), S) => assert(true)
			  case _ => assert(false)
			}
		}
  
		it ("should be self-embeddable") {
			object P extends Process {
				val description = R * this
			}
 
			P.description match {
			  case ConcatenationProcess(R, P) => assert(true)
			  case _ => assert(false)
			}
		}
  
		it ("should have a sum operator") {
			object P extends Process {
				val description = Q + R + S
			}

			P.description match {
			  case SumProcess(SumProcess(Q, R), S) => assert(true)
			  case _ => assert(false)
			}
		}
  
		it ("should be so that concatenation has precedence over sum") {
			object P extends Process {
				val description = Q*R + Q*S
			}

			P.description match {
			  case SumProcess(ConcatenationProcess(Q, R), ConcatenationProcess(Q, S)) => assert(true)
			  case _ => assert(false)
			}
		}
  
  		it ("should have a composition operator") {
			object P extends Process {
				val description = Q | R | S
			}

			P.description match {
			  case CompositionProcess(CompositionProcess(Q, R), S) => assert(true)
			  case _ => assert(false)
			}
		}
  
		it ("should be so that concatenation has precedence over composition") {
			object P extends Process {
				val description = Q*R | Q*S
			}
			
			P.description match {
			  case CompositionProcess(ConcatenationProcess(Q, R), ConcatenationProcess(Q, S)) => assert(true)
			  case _ => assert(false)
			}
		}
	  
	}

}
