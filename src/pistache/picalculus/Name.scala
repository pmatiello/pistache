/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Name class and companion object.
 */

package pistache.picalculus

object Name {
  
	 def apply[T](value:T) = new Name(value)
	 def apply[T] = new Name(null)

	 implicit def unbox[T](name:Name[T]) = name.storedValue
  
	 implicit def box[T](value:T) = Name(value)
  
}

class Name[T](value:T) { 
	val storedValue = value
}
