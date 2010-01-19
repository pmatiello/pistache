/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for calculating factorials.
 */

package pistache.examples

import pistache.picalculus._
import pistache.runner.ThreadedRunner

object Factorial extends Application {

	var n = 0
	var p = 1
  
	val step = Action{
		p = p*n
		n = n-1
	}
 
	val init = Action{
		print("n=")
		n = Console.readInt 
	}
 
	val printResult = Action(println(p))
		
	lazy val F:Process = Process(step*(If (n>1) {F} Else {printResult}))
 
	new ThreadedRunner(init*F) start
  
}
