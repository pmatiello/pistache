/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Integration tests.
 * 
 * These tests aim for checking the interaction between different
 * parts of the framework.
 */


package pistache.integration

import pistache.runner.ThreadedRunner

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class IntegrationTests extends Spec with MustMatchers {
  
	describe ("ThreadedRunner tests") {
		
		it ("Actions, concatenation and recursion") {
			import Factorial._
			n = 10
			new ThreadedRunner(factorialCalculator).start
			result must equal (10*9*8*7*6*5*4*3*2*1)
		}
  
		it ("Message passing and parallel composition") {
			import PingPong._
			new ThreadedRunner(PingPong).start
			result must equal (List.range(0,1000))
		}
	  
	}

}
