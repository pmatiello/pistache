/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for calculating factorials.
 */

package pistache.examples

import pistache.picalculus._
import pistache.runner.SimpleRunner

object Factorial extends Application {

	var n = 0
	var p = 1
  
	val step = Transition{
		p = p*n
		n = n-1
	}
 
	val init = Transition{
		print("n=")
		n = Console.readInt 
	}
 
	val printResult = Transition(println(p))
		
	lazy val F:Process = Process(step*(If (n>1) {F} Else {printResult}))
 
	new SimpleRunner(init*F) start
  
}
