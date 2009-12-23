/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Link class and companion object.
 */

package pistache.picalculus

import Name._

object Link {

	def apply[T] = new Name(new Link[T])
 
	object Action extends Enumeration {
		type Action = Value
		val Send, Receive = Value
	}
}

class Link[T] {
  
	def ^(name:Name[T]) = new LinkProcess(this, Link.Action.Send, name)
 
	def apply(name:Name[T]) = new LinkProcess(this, Link.Action.Receive, name)

}

import Link.Action._

case class LinkProcess[T](link:Link[T], action:Action, name:Name[T]) extends Process {
  
	val description = this
  
}
