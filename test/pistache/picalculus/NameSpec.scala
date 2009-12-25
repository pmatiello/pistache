/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Name class.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import Name._

@RunWith(classOf[JUnitRunner])
class NameSpec extends Spec with MustMatchers {
  
	describe ("Name") {
	  
		it ("should be instantiable without a value") {
			val name = Name[Int]
			assert(true)
		}
	  
		it ("should store an arbitrary value") {
			Name(144).storedValue must equal (144)
		}
  
		it ("should keep the type of the stored value") {
			Name("144").storedValue.getClass must equal (classOf[String])
		}
  
		it ("should autounbox the stored value") {
			val name = Name("abcde")
			name.indexOf("d") must equal (3)
		}
  
  		it ("should autobox a value") {
			val name:Name[String] = "String"
			assert(true)	// We're ok if no exceptions are reaised
		}
  
	}

}
