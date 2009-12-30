/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for calculating factorials.
 */

package pistache.examples

import pistache.picalculus._
import pistache.runner.SimpleRunner

object Restricted extends Application {

	lazy val P:Process = Restriction{
		var x = 0;
		val printX = Transition{
			println(x)
			x = x+1
		}
		Process(printX*P)
	}
 
	new SimpleRunner(P) start
  
}
