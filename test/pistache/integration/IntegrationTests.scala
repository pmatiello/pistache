package pistache.integration

import pistache.runner.ThreadedRunner

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class IntegrationTests extends Spec with MustMatchers {
  
	describe ("ThreadedRunner tests") {
		
		it ("Factorial") {
			import Factorial._
			n = 10
			new ThreadedRunner(factorialCalculator).start
			result must equal (10*9*8*7*6*5*4*3*2*1)
		}
	  
	}

}
