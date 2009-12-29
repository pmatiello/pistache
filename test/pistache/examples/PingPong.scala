/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for message passing and composition.
 */

package pistache.examples

import pistache.picalculus._
import pistache.runner.SimpleRunner

object PingPong {

	private val pingT = Transition{
		n1 := n1.value + 1
		println("Ping! " + n1.value)
	}
	private val pongT = Transition{
		println("Pong! " + n2.value)
	}
	
	private val link1 = Link[Int]
	private val link2 = Link[Int]
	private val n1 = Name[Int](0)
	private val n2 = Name[Int](0)

	lazy val Ping:Process = Process(pingT*link2~n1*link1(n1)*Ping)
	lazy val Pong:Process = Process(link2(n2)*pongT*link1~n2*Pong)
	val PingPong = Process(Ping | Pong)
  
	 def main(args:Array[String]) {
		new SimpleRunner(PingPong) start
	}
  
}
