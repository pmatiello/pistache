/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Agent trait and companion classes.
 */

package pistache.picalculus

import pistache.testing._
import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class AgentSpec extends Spec with MustMatchers {
  
	val Q = new FakePrefix
	val R = new FakePrefix
	val S = new FakeAgent
	val T = new FakeAgent
	
	describe ("Agent") {
  
		it ("should be written as a concatenation of prefixes followed by an agent") {
			val P = Q * R * S
			P match {
				case ConcatenationAgent(left, right) => {
					left.apply match {
						case ConcatenationPrefix(left, right) =>	left.apply must equal (Q)
																	right.apply must equal (R)
					}
					right.apply must equal (S)
				}
			}
		}
  
		it ("should be self-embeddable") {
			lazy val P:Agent = R*P
			P match {
				case ConcatenationAgent(left, right) =>	left.apply must equal(R)
														right.apply must equal (P)
			}
		}
  
		it ("should allow cyclic references") {
			lazy val P:Agent = R*Q
			lazy val Q:Agent = P
			P match {
				case ConcatenationAgent(left, right) =>	left.apply must equal(R)
														right.apply must equal (Q)
			}
		}
  
  		it ("should have a composition operator") {
			val P = S | T | S

			P match {
				case pp:CompositionAgent => {
					pp.left.apply match {
						case CompositionAgent(left, right) =>	left.apply must equal (S)
																right.apply must equal (T)
					}
					pp.right.apply must equal (S)
				}
			}
		}
    
  		it ("should be guardable by a prefix") {
  			val P = Q :: S
  			P match {
  				case GuardedAgent(left, right) =>	left.apply must equal (Q)
  													right.apply must equal (S)
  			}
  		}
    
		it ("should have a sum operator") {
			val G1 = Q::S
			val G2 = R::T
			val G3 = Q::R*T
			val P = G1 + G2 + G3
			P match {
				case SummationAgent(left, right) => {
					left.apply match {
						case SummationAgent(left, right) => {
							left.apply must equal (G1)
							right.apply must equal (G2)
						}
					}
					right.apply must equal (G3)
				}
			}
		}
  
		it ("should be so that concatenation has precedence over composition") {
			val P:Agent = Q*S | R*T
			
			P match {
				case CompositionAgent(left, right) => {
					left.apply match {
						case ConcatenationAgent(left, right) =>	left.apply must be (Q)
																right.apply must be (S)
					}
					right.apply match {
						case ConcatenationAgent(left, right) =>	left.apply must be (R)
																right.apply must be (T)
					}
				}
			}
		}
    
		it ("should be possible to restrict names to agent instances") {
			val P = Agent{
				val x = new Object();
				val l = Link[Object]
				l~x
			}
   
			(P.agent.apply, P.agent.apply) match {
				case Tuple2(ConcatenationAgent(inst1, NilAgent), ConcatenationAgent(inst2, NilAgent)) =>
					(inst1.apply, inst2.apply) match {
						case Tuple2(LinkPrefix(link1, _, name1), LinkPrefix(link2, _, name2)) =>
							link1 must not be theSameInstanceAs (link2)
							name1 must not be theSameInstanceAs (name2)
					}
			}
		}
	}
}
