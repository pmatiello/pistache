/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Name class and companion object.
 */

package pistache.picalculus

/** An object providing methods to create new names, and implicit conversions for autoboxing and autounboxing.
 */
object Name {
  
	/** Create a name containing the given value.
	 * 
	 *  @param value the value to be stored.
	 *  @return the name containing the given value. 
     */
	def apply[T](value:T) = new Name(value)
 
	/** Create an empty name.
	 *
	 *  @return the name containing <code>null</code> as value.
	 */
	def apply[T] = new Name(null.asInstanceOf[T])

	/** Implicit conversion from names to their stored values.
	*
	*  @param name the name.
	*  @return the value stored by the given name.
	*/
	implicit def unbox[T](name:Name[T]) = name.value

	/** Implicit conversion from arbitrary values to names.
	*
	*  @param value the value.
	*  @return the name storing the given value.
	*/
	implicit def box[T](value:T) = Name(value)
  
}

/** A class representing a pi-Calculus name. Names store a reference to an arbitrary value and can be passed
 *  through links.
 * 
 *	@param storage the value stored by the Name object.
 */
class Name[T](var storage:T) { 
	
	/** Update the stored value.
	 *
	 *  @param value the new stored value. 
	 */
	def :=(value:T) { storage = value }
 
	/** Read the stored value.
	 */
	def value = storage
}
