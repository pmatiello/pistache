/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Name class.
 */

package pistache.names

import org.scalatest.Spec

import Name._

class NameSpec extends Spec {
  
	describe ("Name") {
	  
		it ("should be instantiable without a value") {
			val name = Name[Int]
			assert(true)
		}
	  
		it ("should store an arbitrary value") {
			assert(Name(144).storedValue == 144)
		}
  
		it ("should keep the type of the stored value") {
			assert(Name("144").storedValue.getClass == classOf[String])
		}
  
		it ("should autounbox the stored value") {
			val name = Name("abcde")
			assert(name.indexOf("d") == 3)
		}
  
  		it ("should autobox a value") {
			val name:Name[String] = "String"
			assert(true)	// We're ok if no exceptions are reaised
		}
  
	}

}
