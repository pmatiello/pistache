/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for agents with arguments and massive threading.
 */

package pistache.integration

import pistache.picalculus._

class Quicksort(list:List[Int]) {

	def qsort(replyLink:Link[List[Int]], list:List[Int]):Agent = Agent {
		val leftLink = Link[List[Int]]
		val leftList = Name[List[Int]]
		val rightLink = Link[List[Int]]
		val rightList = Name[List[Int]]
  
		val filter = Action{
			leftList := list.tail filter (_ <= list.head)
			rightList := list.tail filter (_ > list.head)
		}
		(If (list.size > 1) { filter * (
			qsort(leftLink, leftList.value) |
			qsort(rightLink, rightList.value) |
			leftLink(leftList)*rightLink(rightList) *
			replyLink~(leftList ::: list.head :: rightList))
		} * If (list.size <= 1) {replyLink~list})
	}
	
	val replyLink = Link[List[Int]] 
	val result = Name[List[Int]]
	val agent = Agent(qsort(replyLink, list) | replyLink(result))
}
