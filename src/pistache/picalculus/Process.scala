/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Process class and companion object.
 */

package pistache.picalculus

//object Process {
//
//	def apply(description:List[AtomicProcess]) = new Process(description) 
//  
//}

abstract class Process {
	 val description:List[AtomicProcess]
}
