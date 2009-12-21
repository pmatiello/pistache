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
  
		it ("should be written as a concatenation of Atomic Processes") {
			object P extends Process {
				val ap1 = mock(classOf[AtomicProcess])
				val ap2 = mock(classOf[AtomicProcess])
				val ap3 = mock(classOf[AtomicProcess])
				val description = ap1 :: ap2 :: ap3 :: Nil
			}
			assert(true)
		}
	  
	}

}
