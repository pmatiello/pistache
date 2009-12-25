/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Link class and companion object.
 */

package pistache.picalculus

import Name._

/** An object providing methods to create new links.
 */
object Link {

	/** Create a link of the given type.
	 * 
	 *  @return the name containing the link for sending and
	 *  receiving objects from type <code>T</code>.
     */
	def apply[T] = new Name(new Link[T])
 
	/** Enumeration for representing the possible actions on a
	 *  link: sending and receiving messages.
     */
	object Action extends Enumeration {
		type Action = Value
		val Send, Receive = Value
	}
}

/** A class representing a pi-Calculus link. Links can be used
 *  to send names between processes.
 */
class Link[T] {
  
	def ^(name:Name[T]) = new LinkProcess(this, Link.Action.Send, name)
 
	def apply(name:Name[T]) = new LinkProcess(this, Link.Action.Receive, name)

}

import Link.Action._

case class LinkProcess[T](link:Link[T], action:Action, name:Name[T]) extends Process {
  
	val description = this
  
}
