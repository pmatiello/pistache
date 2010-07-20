/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test programs for very deep recursion.
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

class CompositionSummation(list:List[Int]) {

	var sum = 0
 
	val dummy = Action {}
  
	def step(list:List[Int]):Agent = Agent {
		val add = Action{ sum += list.head }
		If (!list.isEmpty) {add*(step(list.tail) | dummy)}
	}
 
	val agent = Agent(step(list))
}
