/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Test program for massive threading.
 */

package pistache.integration

import pistache.picalculus._

class Quicksort(list:List[Int]) {

	def qsort(replyLink:Link[List[Int]], list:List[Int]):Process = Process {
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
		} Else {replyLink~list})
	}
	
	val replyLink = Link[List[Int]] 
	val result = Name[List[Int]]
	val process = Process(qsort(replyLink, list) | replyLink(result))
}
