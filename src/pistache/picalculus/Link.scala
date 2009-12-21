/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Link class and companion object.
 */

package pistache.picalculus

import Name._

object Link {

	def apply[T] = new Name(new Link[T])

}

class Link[T] {
  
	def ^(name:Name[T]) = new LinkProcess(this, name)
 
	def apply(name:Name[T]) = new LinkProcess(this, name)

}
