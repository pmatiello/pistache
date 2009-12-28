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

object Factorial extends Application {

	private var p = 1
	private var n = 10
  
	private val silent = Transition{
		p = p*n
		n = n-1
	}
 
	private val printResult = Transition(println(p))
		
	lazy val F:Process = Process(silent*(If (n>1) {F} Else {printResult}))
 
	
 
	new SimpleRunner(F) start
  
}
