/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for actions, concatenation and recursion.
 */

package pistache.integration

import pistache.picalculus._

object Factorial {

	var n = 0
	var result = 1
  
	val step = Action{
		result = result*n
		n = n-1
	}
	
	lazy val factorialCalculator:Process = Process(step*(If (n>1) {factorialCalculator}))
  
}
