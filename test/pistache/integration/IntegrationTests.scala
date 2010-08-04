/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Integration tests.
 * 
 * These tests aim for checking the interaction between different parts of the framework.
 */


package pistache.integration

import pistache.runner.ThreadedRunner

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import java.util.Random

@RunWith(classOf[JUnitRunner])
class IntegrationTests extends Spec with MustMatchers {
  
	val random = new Random()
	def randomList(size:Int):List[Int] = if (size > 0) random.nextInt(10000) :: randomList(size-1) else Nil
  
	describe ("ThreadedRunner tests") {
		
		it ("Actions, concatenation and recursion") {
			val factorialCalculator = new Factorial(10)
			new ThreadedRunner(factorialCalculator.agent).start
			factorialCalculator.result must equal (10*9*8*7*6*5*4*3*2*1)
		}
		
		it ("Message passing and parallel composition") {
			val PingPong = new PingPong(1000)
			new ThreadedRunner(PingPong.agent).start
			PingPong.result must equal (List.range(0,1000))
		}
  
		it ("Message passing and channel locking") {
			val ProducerConsumer = new ProducerConsumer(500000)
			new ThreadedRunner(ProducerConsumer.agent).start
		}
  
		it ("Message passing and complementary sums") {
			val ProducerConsumer = new ProducerConsumerWithComplementarySums(500000)
			new ThreadedRunner(ProducerConsumer.agent).start
		}
  
		it ("Message passing and input guarded sums") {
			val ProducerConsumer = new ProducerConsumerWithInputSums(500000)
			new ThreadedRunner(ProducerConsumer.agent).start
		}
  
		it ("Message passing and output guarded sums") {
			val ProducerConsumer = new ProducerConsumerWithOutputSums(500000)
			new ThreadedRunner(ProducerConsumer.agent).start
		}
  
		it ("Agents with arguments and massive threading") {
			val unsortedList = randomList(300) 
			val qsort = new Quicksort(unsortedList)
			new ThreadedRunner(qsort.agent).start
			qsort.result.value must equal (unsortedList.sortWith(_ < _))
		}
  
		it ("Deep recursion by concatenation") {
			val list = 1 to 100000 toList
			val summation = new Summation(list)
			new ThreadedRunner(summation.agent).start
			summation.sum must equal (list.reduceLeft(_+_))
		}
  
		it ("Deep recursion by composition") {
			val list = 1 to 10000 toList
			val summation = new CompositionSummation(list)
			new ThreadedRunner(summation.agent).start
			summation.sum must equal (list.reduceLeft(_+_))
		}
	  
	}

}
