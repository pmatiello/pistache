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
	object ActionType extends Enumeration {
		type ActionType = Value
		val Send, Receive = Value
	}
}

/** A class representing a pi-Calculus link. Links can be used
 *  to send names between processes.
 */
protected[pistache] class Link[T] {
  
	/** Send a name through this link.
	 * 
	 *  @param name the name to be sent through the link.
	 *  @return the process representing this action.
     */
	def ~(name:Name[T]) = new LinkProcess(this, Link.ActionType.Send, name)
	
	/** Receive a name through this link.
	 * 
	 *  @param name the name to be used as storage of the
	 *  object received through the link.
	 *  @return the process representing this action.
     */
	def apply(name:Name[T]) = new LinkProcess(this, Link.ActionType.Receive, name)

}

import Link.ActionType._

/** A class representing an action over a link as an atomic
 *  pi-Calculus process.
 * 
 *  @param linkObj the link where the action took place.
 *  @param actionType the type of action.
 *  @param nameObj the name involved in the transference.
 */
protected[pistache] class LinkProcess[T](linkObj:Link[T], actionType:ActionType, nameObj:Name[T]) extends Process {
	val link = linkObj;
	val action = actionType
	val name = nameObj
}