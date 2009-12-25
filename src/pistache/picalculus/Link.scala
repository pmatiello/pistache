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
  
	/** Send a name through this link.
	 * 
	 *  @param name the name to be sent through the link.
	 *  @return the process representing this action.
     */
	def ^(name:Name[T]) = new LinkProcess(this, Link.Action.Send, name)
	
	/** Receive a name through this link.
	 * 
	 *  @param name the name to be used as storage of the
	 *  object received through the link.
	 *  @return the process representing this action.
     */
	def apply(name:Name[T]) = new LinkProcess(this, Link.Action.Receive, name)

}

import Link.Action._

/** A class representing an action over a link as an atomic
 *  pi-Calculus process.
 * 
 *  @param link the link where the action took place.
 *  @param action the type of action.
 *  @param name the name involved in the transference.
 */
case class LinkProcess[T](link:Link[T], action:Action, name:Name[T]) extends Process {
	
	/** Process description.*/
	val description = this
}
