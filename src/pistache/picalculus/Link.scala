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
	 *  @return the name containing the link for sending and receiving objects from type <code>T</code>.
     */
	def apply[T] = new Name(new Link[T])
 
	/** Enumeration for representing the possible actions on a link: sending and receiving messages.
     */
	object ActionType extends Enumeration {
		type ActionType = Value
		val Send, Receive = Value
	}
}

/** A class representing a pi-Calculus link. Links can be used to send names between agents.
 */
class Link[T] {
  
	/** Send a name through this link.
	 * 
	 *  @param name the name to be sent through the link.
	 *  @return the agent representing this action.
     */
	def ~(name:Name[T]) = new LinkPrefix(this, Link.ActionType.Send, name)
 
	/** Send an empty message through this link.
	 * 
	 *  @return the agent representing this action.
     */
	def ~() = new LinkPrefix(this, Link.ActionType.Send, null)
	
	/** Receive a name through this link.
	 * 
	 *  @param name the name to be used as storage of the object received through the link.
	 *  @return the agent representing this action.
     */
	def apply(name:Name[T]) = new LinkPrefix(this, Link.ActionType.Receive, name)
 
	/** Receive an empty message through this link.
	 * 
	 *  @return the agent representing this action.
     */
	def apply() = new LinkPrefix(this, Link.ActionType.Receive, Name[T])


}

import Link.ActionType._

/** A class representing an action over a link as an atomic pi-Calculus prefix.
 * 
 *  @param link the link where the action took place.
 *  @param action the type of action.
 *  @param name the name involved in the transference.
 */
case class LinkPrefix[T](val link:Link[T], val action:ActionType, val name:Name[T]) extends Prefix