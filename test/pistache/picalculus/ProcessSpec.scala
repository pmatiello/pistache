/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec for Process trait and companion classes.
 */

package pistache.picalculus

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class ProcessSpec extends Spec with MustMatchers {
  
	val Q = new Process
	val R = new Process
	val S = new Process
	
	describe ("Process") {
  
		it ("should be written as a concatenation of Processes") {
			val P = Process(Q * R * S)
			P match {
				case pp:ConcatenationProcess => {
					pp.left match {
						case pl:ConcatenationProcess => pl.left must equal (Q)
														pl.right must equal (R)
					}
					pp.right must equal (S)
				}
			}
		}
  
		it ("should be self-embeddable") {
			lazy val P:Process = Process(R*P)
			P match {
				case pp:ConcatenationProcess => pp.left must equal(R)
												pp.right must equal (P)
			}
		}
  
		it ("should allow cyclic references") {
			lazy val P:Process = Process(R*Q)
			lazy val Q:Process = Process(P)
			P match {
				case pp:ConcatenationProcess => pp.left must equal(R)
												pp.right must equal (Q)
			}
		}
  
  		it ("should have a composition operator") {
			val P = Process(Q | R | S)

			P match {
				case pp:CompositionProcess => {
					pp.left match {
						case pl:CompositionProcess => pl.left must equal (Q)
														pl.right must equal (R)
					}
					pp.right must equal (S)
				}
			}
		}
  
		it ("should be so that concatenation has precedence over composition") {
			val P:Process = Process(Q*R | Q*S)
			
			P match {
				case pp:CompositionProcess => {
					pp.left match {
						case pl:ConcatenationProcess => pl.left must be (Q)
														pl.right must be (R)
					}
					pp.right match {
						case pr:ConcatenationProcess => pr.left must be (Q)
														pr.right must be (S)
					}
				}
			}
		}
  
		it ("should be possible to restrict names to process instances") {
			val P = Restriction{
				val x = new Object();
				val l = Link[Object]
				Process(l~x)
			}
			val inst1 = P.process.apply
			val inst2 = P.process.apply
			inst1 must not be theSameInstanceAs (inst2)
			inst1.asInstanceOf[LinkProcess[Object]].link must not be theSameInstanceAs (inst2.asInstanceOf[LinkProcess[Object]].link)
			inst1.asInstanceOf[LinkProcess[Object]].name must not be theSameInstanceAs (inst2.asInstanceOf[LinkProcess[Object]].name) 
		}
	}
}
