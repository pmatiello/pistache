/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for message passing and parallel composition.
 */

package pistache.integration

import pistache.picalculus._
import pistache.runner.ThreadedRunner

class PingPong(max:Int) {

	val pingT = Action{
		result = result ::: n1.value :: Nil
		n1 := n1.value + 1
	}
	val pongT = Action{
		result = result ::: n2.value :: Nil
		n2 := n2.value + 1
	}
	
	val link1 = Link[Int]
	val link2 = Link[Int]
	val n1 = Name[Int](0)
	val n2 = Name[Int](0)
 
	var result:List[Int] = Nil

	lazy val Ping:Agent = Agent(pingT*link2~n1*link1(n1)*If (n1 < max) {Ping})
	lazy val Pong:Agent = Agent(link2(n2)*pongT*link1~n2*If (n2 < max) {Pong})
	val agent = Agent(Ping | Pong)
  
}
