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
  
	describe ("Process") {
  
		it ("should be written as a concatenation of Processes") {
			object P extends Process {
				val ap1 = mock(classOf[Process])
				val ap2 = mock(classOf[Process])
				val ap3 = mock(classOf[Process])
				val description = ap1 :: ap2 :: ap3 :: Nil
			}
			assert(P.toString != null)	// force instantiation of object
		}
  
		it ("should be self-embeddable") {
			object P extends Process {
				val ap = mock(classOf[Process])
				val description = ap :: this :: Nil
			}
			assert(P.toString != null)	// force instantiation of object
		}
  
		it ("should have a sum operator") {
			object S extends Process {
				val description = Nil 
			}
			object R extends Process {
				val description = Nil 
			}
			object Q extends Process {
				val description = R + S :: Nil 
			}
			object P extends Process {
				val ap = mock(classOf[Process])
				val description = ap :: Q + R + S :: Nil
			}
			assert(Q.description(0).description(0) == R)
			assert(Q.description(0).description(1) == S)
			assert(P.description(0) == P.ap)
			assert(P.description(1).description(0).description(0) == Q)
			assert(P.description(1).description(0).description(1) == R)
			assert(P.description(1).description(1) == S)
		}
	  
	}

}
