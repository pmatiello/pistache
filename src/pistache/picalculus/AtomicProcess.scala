/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Atomic process stuff.
 */

package pistache.picalculus

trait AtomicProcess extends Process {
	val description = Nil 
}

case class LinkProcess[T](link:Link[T], name:Name[T]) extends AtomicProcess