/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for testing processes with restricted names.
 */

package pistache.examples

import pistache.picalculus._
import pistache.runner.ThreadedRunner

object Restricted extends Application {

	lazy val P:Process = Restriction{
		var x = 0;
		val printX = Action{
			println(x)
			x = x+1
		}
		Process(printX*P)
	}
 
	new ThreadedRunner(P) start
  
}
