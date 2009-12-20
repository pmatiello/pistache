/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Name class and companion object.
 */

package pistache.names

object Name {
  
  def apply[T](value:T) = new Name(value)

  implicit def unbox[T](name:Name[T]) = name.storedValue
  
}

class Name[T](value:T) { 
	val storedValue = value
}