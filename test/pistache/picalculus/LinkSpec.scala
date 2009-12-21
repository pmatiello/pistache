/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Link class.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import Name._

@RunWith(classOf[JUnitRunner])
class LinkSpec extends Spec {
  
	describe ("Link") {
	  
		it ("should be associable to a Name") {
			assert(Link[String].isInstanceOf[Name[_]])
		}
  
		it ("should support sending messages") {
			val link = Link[Int]
			link^5
			assert(true)	// We're ok if no exceptions are reaised
		}
  
		it ("should support receiving messages") {
			val link = Link[Int]
			val name = Name[Int]
			link(name)
			assert(true)	// We're ok if no exceptions are reaised
		}
  
		it ("should return a LinkProcess when ^ (send) is called") {
			val link = Link[Int]
			val name = Name(5)
			val process:LinkProcess[Int] = link^name
			assert(process == LinkProcess(link, name))
		}
  
  		it ("should return a LinkProcess when apply (receive) is called") {
			val link = Link[Int]
			val name = Name[Int]
			val process:LinkProcess[Int] = link(name)
			assert(process == LinkProcess[Int](link, name))
  		}
  
	}

}
