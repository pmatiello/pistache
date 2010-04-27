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
 	val Q = new FakePrefix
  
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
			val link1 = Link[Int]
			val name1 = Name(5)
			val agent:Agent = link1~name1
			agent match {
				case LinkAgent(link, action, name) =>	link must equal (link1.value)
														action must equal (Link.ActionType.Send)
														(name == name1) must equal (true)
			}
		}
  
  		it ("should return a Agent when apply (receive) is called") {
			val link1 = Link[Int]
			val name1 = Name[Int]
			val agent:Agent = link1(name1)
			agent match {
				case LinkAgent(link, action, name) =>	link must equal (link1.value)
														action must equal (Link.ActionType.Receive)
														(name == name1) must equal (true)
			}
  		}
    
  		it ("should be so that send has precedence over agent concatenation") {
  			val link1 = Link[Int]
  			val name1 = Name(5)
			val agent:Agent = Q * link1~name1 * P
			agent match {
				case ConcatenationAgent(left, right) => {
					left.apply match {
						case ConcatenationAgent(left, right) =>	left.apply must equal (Q)
																right.apply match {
																case LinkAgent(link, action, name) =>	link must equal (link1.value)
																										action must equal (Link.ActionType.Send)
																										(name == name1) must equal (true)
																}
					}
					right.apply must equal (P)
				}
			}
  		}
    
  		it ("should be so that receive has precedence over agent concatenation") {
  			val link1 = Link[Int]
  			val name1 = Name[Int]
			val agent:Agent = Q * link1(name1) * P
   
			agent match {
				case ConcatenationAgent(left, right) => {
					left.apply match {
						case ConcatenationAgent(left, right) => left.apply must equal (Q)
																right.apply match {
																case LinkAgent(link, action, name) =>	link must equal (link1.value)
																											action must equal (Link.ActionType.Receive)
																											(name == name1) must equal (true)
																}
					}
					right.apply must equal (P)
				}
			}
  		}
  
	}

}
