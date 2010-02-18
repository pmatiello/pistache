/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for very deep recursion.
 */

package pistache.integration

import pistache.picalculus._

class Summation(list:List[Int]) {

	var sum = 0
  
	def step(list:List[Int]):Agent = Agent {
		val add = Action{ sum += list.head }
		If (!list.isEmpty) {add*step(list.tail)}
	}
 
	val agent = Agent(step(list))
}
