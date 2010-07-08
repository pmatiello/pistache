/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program message passing and channel locking.
 */

package pistache.integration

import pistache.picalculus._

class ProducerConsumer(limit:Int) {

	 private val link = Link[Int]
	 private var finished = false
  
	 lazy val P:Agent = Agent(link~0*If(!finished) {P})
  
	 val Q = Agent {

		 var iter = 0
		 val n = Name[Int]

		 val act = Action { 
			 iter = iter + 1
			 if (iter > limit) {
			   finished = true
			 }
		 }
   
		 lazy val q:Agent = Agent(act*link(n)*If(iter <= limit) {q})
		 q
	 }
  
	 val agent = Agent(P | P | Q)
}

class ProducerConsumerWithComplementarySums(limit:Int) {

	 private val link = Link[Any]
	 private var lock:AnyRef = new Object
	 private var count = 0
  
	 val act = Action {
		 lock.synchronized {
			 count = count + 1
		 }
	 }
  
	 lazy val outSum:Agent = (link~() :: If (count < limit) {outSum}) + (link~() :: If (count < limit) {outSum})
	 lazy val inSum:Agent = (link() :: act * If (count < limit) {inSum}) + (link() :: act * If (count < limit) {inSum})
  
  
	 val agent = Agent(outSum | inSum)
}

class ProducerConsumerWithInputSums(limit:Int) {

	 private val link1 = Link[Any]
	 private val link2 = Link[Any]
	 private var lock:AnyRef = new Object
	 private var count = 0
  
	 val act = Action {
		 lock.synchronized {
			 count = count + 1
		 }
	 }
  
	 val producer1:Agent = link1~() * If (count < limit) {producer2}
	 val producer2:Agent = link2~() * If (count < limit) {producer1}
	 lazy val inSum:Agent = (link1() :: act * If (count < limit) {inSum}) + (link2() :: act * If (count < limit) {inSum})
  
  
	 val agent = Agent(producer1 | inSum)
}