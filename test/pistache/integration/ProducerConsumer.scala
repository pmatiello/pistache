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
