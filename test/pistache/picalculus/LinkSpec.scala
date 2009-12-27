/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Link class.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import Name._

@RunWith(classOf[JUnitRunner])
class LinkSpec extends Spec with MustMatchers {
  
 	val P = new Process {
		val description = null 
	}
  
	describe ("Link") {
	  
		it ("should be associable to a Name") {
			assert(Link[String].isInstanceOf[Name[_]])
		}
  
		it ("should support sending messages") {
			val link = Link[Int]
			link~5
			assert(true)	// We're ok if no exceptions are reaised
		}
  
		it ("should support receiving messages") {
			val link = Link[Int]
			val name = Name[Int]
			link(name)
			assert(true)	// We're ok if no exceptions are reaised
		}
  
		it ("should return a Process when ~ (send) is called") {
			val link = Link[Int]
			val name = Name(5)
			val process:Process = link~name
			process must equal (LinkProcess(link, Link.Action.Send, name))
		}
  
  		it ("should return a Process when apply (receive) is called") {
			val link = Link[Int]
			val name = Name[Int]
			val process:Process = link(name)
			process must equal (LinkProcess[Int](link, Link.Action.Receive, name))
  		}
    
  		it ("should be so that send has precedence over process concatenation") {
  			val link = Link[Int]
  			val name = Name(5)
			val process:Process = P * link~name * P
			process must equal (ConcatenationProcess(
									ConcatenationProcess(P, LinkProcess(link, Link.Action.Send, name)),
									P))
  		}
    
  		it ("should be so that receive has precedence over process concatenation") {
  			val link = Link[Int]
  			val name = Name[Int]
			val process:Process = P * link(name) * P
			process must equal (ConcatenationProcess(
									ConcatenationProcess(P, LinkProcess[Int](link, Link.Action.Receive, name)),
									P))
  		}
  
	}

}
