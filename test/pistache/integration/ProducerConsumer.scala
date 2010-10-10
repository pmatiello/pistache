/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program message passing and channel locking.
 */

package pistache.integration

import pistache.picalculus._

class ProducerConsumer(limit:Int) {

	 private val link = Link[Int]
  
	 lazy val P:Agent = Agent{
		 
		 var iter = 0
		 val act = Action {
			 iter = iter+1
		 }
		 
		 lazy val p:Agent = link~0*act*If(iter < limit/2) {p}
		 p
	 }
  
	 val Q = Agent {

		var iter = 0
		val n = Name[Int]

		val act = Action { 
			 iter = iter + 1
		}
   
		lazy val q:Agent = Agent(act*link(n)*If(iter < limit) {q})
		q
	 }
  
	 val agent = Agent(P | P | Q)
}

class ProducerConsumerWithComplementarySums(limit:Int) {

	private val link1 = Link[Any]
	private val link2 = Link[Any]
	private var countp = 0
	private var countc = 0
  
	val actp = Action {
		countp = countp + 1
	}
	
	val actc = Action {
		countc = countc + 1
	}
	  
	lazy val outSum1:Agent = (link1~() :: actp * If (countp < limit) {outSum2}) + (link2~() :: actp * If (countp < limit) {outSum2})
	lazy val outSum2:Agent = (link2~() :: actp * If (countp < limit) {outSum1}) + (link1~() :: actp * If (countp < limit) {outSum1})
	lazy val inSum:Agent = (link1() :: actc * If (countc < limit) {inSum}) + (link2() :: actc * If (countc < limit) {inSum})
  
  
	val agent = Agent(outSum1 | inSum)
}

class ProducerConsumerWithInputSums(limit:Int) {

	private val link1 = Link[Any]
	private val link2 = Link[Any]
	private var countp = 0
	private var countc = 0
  
	val actp = Action {
		countp = countp + 1
	}
	
	val actc = Action {
		countc = countc + 1
	}
	
	val producer1:Agent = link1~() * actp * If (countp < limit) {producer2}
	val producer2:Agent = link2~() * actp * If (countp < limit) {producer1}
	lazy val inSum:Agent = (link1() :: actc * If (countc < limit) {inSum}) + (link2() :: actc * If (countc < limit) {inSum})
  
	val agent = Agent(producer1 | inSum)
}

class ProducerConsumerWithOutputSums(limit:Int) {

	private val link1 = Link[Any]
	private val link2 = Link[Any]
	private var countp = 0
	private var countc = 0
  
	val actp = Action {
		countp = countp + 1
	}
	
	val actc = Action {
		countc = countc + 1
	}
  
	val consumer1:Agent = link1() * actc * If (countc < limit) {consumer2}
	val consumer2:Agent = link2() * actc * If (countc < limit) {consumer1}
	lazy val outSum:Agent = (link1~() :: actp * If (countp < limit) {outSum}) + (link2~() :: actp * If (countp < limit) {outSum})
	
	val agent = Agent(consumer1 | outSum)
}