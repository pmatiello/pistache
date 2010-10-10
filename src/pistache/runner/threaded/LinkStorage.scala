package pistache.runner.threaded

import pistache.picalculus._
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

/** A centralized storage for stuff transmitted through links.
 */
protected object LinkStorage {

	/** A concrete implementation of links.
	 */
	class LinkImplementation {
		private var buffer:Any = null
		private var empty = true
		private var blocked = false
		private var writer:AnyRef = null
		private var reader:AnyRef = null
		private var lock:AnyRef = new Object
		
		/** Make the thread wait until the given condition is satisfied.
		 *
		 *  @param cond the condition 
		 */
		private def waitUntil(cond : => Boolean) {
			while (!cond) { lock.wait }
		}
		
		/** Send (store) a value through the link.
		 *
		 *  @param value the value 
		 */
		def send(value:Any) {
			lock.synchronized {
				waitUntil(empty && (writer == null || writer == Thread.currentThread))
				writer = Thread.currentThread
				buffer = value
				empty = false
				lock.notifyAll
				waitUntil(empty)
				writer = null
				blocked = false
				lock.notifyAll
			}
		}
  
		/** Send (store) a value through the link.
		 *  
		 *  This function will try only once.
		 *
		 *  @param value the value
		 *  @return whether the sending was successful
		 */
		def guardedSend(value:Any) = {
			lock.synchronized {
				if (writer == null || empty) {
					writer = Thread.currentThread
				}
				if (empty && writer == Thread.currentThread && (reader != null && reader != Thread.currentThread)) {
					send(value)
					true
				} else {
			    	false
			    }
			}
		}

		/** Receive (retrieve) a value through the link.
		 * 
		 *  @return a previously sent value 
		 */
		def recv:Any = {
			lock.synchronized {
				reader = Thread.currentThread
				waitUntil (!empty)
				val temp = buffer
				empty = true
				reader = null
				blocked = true
				lock.notifyAll
				temp
			}
		}
  
		/** Receive (retrieve) a value through the link.
		 *
   		 *  This function will try only once.
		 * 
		 *  @return Tuple containing: (success status, a previously sent value) 
		 */
		def guardedRecv:Option[Any] = {
			lock.synchronized {
				if (!blocked && writer != null && writer != Thread.currentThread) {
					if (!empty) {
						Some(recv)  
					} else {
						reader = Thread.currentThread
						None
					}
				} else {
					None
				}
			}
		}
  
	}
  
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