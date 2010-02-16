/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Link class.
 */

package pistache.picalculus

import pistache.testing._
import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import Name._

@RunWith(classOf[JUnitRunner])
class LinkSpec extends Spec with MustMatchers {
  
 	val P = new FakeAgent
  
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
  
		it ("should return a Agent when ~ (send) is called") {
			val link = Link[Int]
			val name = Name(5)
			val agent:Agent = link~name
			agent match {
				case pp:LinkAgent[_] =>	pp.link must equal (link.value)
										pp.action must equal (Link.ActionType.Send)
										(pp.name == name) must equal (true)
			}
		}
  
  		it ("should return a Agent when apply (receive) is called") {
			val link = Link[Int]
			val name = Name[Int]
			val agent:Agent = link(name)
			agent match {
				case pp:LinkAgent[_] =>	pp.link must equal (link.value)
										pp.action must equal (Link.ActionType.Receive)
										(pp.name == name) must equal (true)
			}
  		}
    
  		it ("should be so that send has precedence over agent concatenation") {
  			val link = Link[Int]
  			val name = Name(5)
			val agent:Agent = P * link~name * P
			agent match {
				case p:ConcatenationAgent => {
					p.left match {
						case pp:ConcatenationAgent =>	pp.left must equal (P)
														pp.right match {
															case pr:LinkAgent[_] =>	pr.link must equal (link.value)
																					pr.action must equal (Link.ActionType.Send)
																					(pr.name == name) must equal (true)
														}
					}
					p.right must equal (P)
				}
			}
  		}
    
  		it ("should be so that receive has precedence over agent concatenation") {
  			val link = Link[Int]
  			val name = Name[Int]
			val agent:Agent = P * link(name) * P
   
			agent match {
				case p:ConcatenationAgent => {
					p.left match {
						case pp:ConcatenationAgent => pp.left must equal (P)
														pp.right match {
															case pr:LinkAgent[_] =>	pr.link must equal (link.value)
																					pr.action must equal (Link.ActionType.Receive)
																					(pr.name == name) must equal (true)
														}
					}
					p.right must equal (P)
				}
			}
  		}
  
	}

}
