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
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class AgentSpec extends Spec with MustMatchers {
  
	val Q = new FakeAgent
	val R = new FakeAgent
	val S = new FakeAgent
	
	describe ("Agent") {
  
		it ("should be written as a concatenation of agents") {
			val P = Q * R * S
			P match {
				case ConcatenationAgent(left, right) => {
					left.apply match {
						case ConcatenationAgent(left, right) =>	left.apply must equal (Q)
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
			val P = Q | R | S

			P match {
				case pp:CompositionAgent => {
					pp.left.apply match {
						case CompositionAgent(left, right) =>	left.apply must equal (Q)
																right.apply must equal (R)
					}
					pp.right.apply must equal (S)
				}
			}
		}
  
		it ("should be so that concatenation has precedence over composition") {
			val P:Agent = Q*R | Q*S
			
			P match {
				case CompositionAgent(left, right) => {
					left.apply match {
						case ConcatenationAgent(left, right) =>	left.apply must be (Q)
																right.apply must be (R)
					}
					right.apply match {
						case ConcatenationAgent(left, right) =>	left.apply must be (Q)
																right.apply must be (S)
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
			val inst1 = P.agent.apply
			val inst2 = P.agent.apply
			inst1 must not be theSameInstanceAs (inst2)
			inst1.asInstanceOf[LinkAgent[Object]].link must not be theSameInstanceAs (inst2.asInstanceOf[LinkAgent[Object]].link)
			inst1.asInstanceOf[LinkAgent[Object]].name must not be theSameInstanceAs (inst2.asInstanceOf[LinkAgent[Object]].name) 
		}
	}
}
