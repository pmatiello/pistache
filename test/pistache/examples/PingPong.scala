/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for calculating factorials.
 * 
 * Not really very pi-Calculus-ish yet, but it's enough to test the runner a bit.
 */

package pistache.examples

import pistache.picalculus._
import pistache.runner.SimpleRunner

object PingPong extends Application {

	private val pingT = Transition{
		println("Ping!")
	}
	private val pongT = Transition{
		println("Pong!")
	}
	private val link = Link[Int]
	private val n1 = Name(1)
	private val n2 = Name(2)

	lazy val Ping:Process = Process(pingT*link~n1*link(n1)*Ping)
	lazy val Pong:Process = Process(link(n2)*pongT*link~n2*Pong)
	val PingPong = Process(Ping | Pong)
 
	new SimpleRunner(PingPong) start
  
}
