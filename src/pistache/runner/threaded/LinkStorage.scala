package pistache.runner.threaded

import pistache.picalculus._
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

/** A centralized storage for stuff transmitted through links.
 */
protected object LinkStorage {
  
	private var links:Map[Link[_], LinkImplementation] = null
	private val lock:AnyRef = new Object
  
	/** Initialize the storage. 
	 */
	def initialize() {
		links = new HashMap[Link[_], LinkImplementation]
	}

	/** Associate a link to an implementation, if it's not yet associated. 
	 */
	private def ready[T](link:Link[T]) {
		lock.synchronized {
			if (!links.keySet.contains(link)) {
				links += link -> new LinkImplementation
			}
			lock.notifyAll
		}
	}

	/** Send the given name through the given link.
	 * 
	 *  @param link the link
	 *  @param name the name
	 */
	def send[T](link:Link[T], name:Name[T]) {
		ready(link)
		links(link).send(if (name != null) name.value else null)
	}
 
	/** Send the given name through the given link.
	 * 
	 *  This function will try only once.
	 *
	 *  @param link the link
	 *  @param name the name
	 * 
	 *  @return whether the sending was successful
	 */
	def guardedSend[T](link:Link[T], name:Name[T]) = {
		ready(link)
		links(link).guardedSend(if (name != null) name.value else null)
	}


	/** Receive a value from the given link and store it on the given name.
	 * 
	 *  @param link the link
	 *  @param name the name
	 */
	def recv[T](link:Link[T], name:Name[T]) {
		ready(link)
		name := links(link).recv.asInstanceOf[T]
	}
 
	/** Receive a value from the given link and store it on the given name.
	 *
	 *  This function will try only once.
	 *
	 *  @param link the link
	 *  @param name the name
	 * 
	 *  @return whether the sending was successful
	 */
	def guardedRecv[T](link:Link[T], name:Name[T]):Boolean = {
		ready(link)
		links(link).guardedRecv match {
			case Some(value)	=> name := value.asInstanceOf[T]; true
			case None			=> false
		}
	}
  
}