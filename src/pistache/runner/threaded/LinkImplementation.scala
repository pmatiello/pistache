package pistache.runner.threaded

/** A concrete implementation of links.
 */
protected class LinkImplementation {
	private var buffer: Any = null
	private var empty = true
	private var blocked = false
	private var writer:Thread = null
	private var reader:Thread = null

	/** Make the thread wait until the given condition is satisfied.
	 *
	 *  @param cond the condition 
	 */
	private def waitUntil(cond: => Boolean) {
		while (!cond) { wait }
	}

	/** Send (store) a value through the link.
	 *
	 *  @param value the value 
	 */
	def send(value: Any): Boolean = {
		synchronized {
			waitUntil(empty && (writer == null || writer == Thread.currentThread))
			writer = Thread.currentThread
			buffer = value
			empty = false
			notifyAll
			waitUntil(empty)
			writer = null
			blocked = false
			notifyAll
		}
		true
	}

	/** Send (store) a value through the link.
	 *  
	 *  This function will try only once.
	 *
	 *  @param value the value
	 *  @return whether the sending was successful
	 */
	def guardedSend(value: Any): Boolean = {
		synchronized {
			if (writer == null || empty)
				writer = Thread.currentThread
			if (empty && writer == Thread.currentThread && (reader != null && reader != Thread.currentThread))
				return send(value)
		}
		return false
	}

	/** Receive (retrieve) a value through the link.
	 * 
	 *  @return a previously sent value 
	 */
	def recv: Any = {
		synchronized {
			reader = Thread.currentThread
			waitUntil(!empty)
			val temp = buffer
			empty = true
			reader = null
			blocked = true
			notifyAll
			temp
		}
	}

	/** Receive (retrieve) a value through the link.
	 *
	 *  This function will try only once.
	 * 
	 *  @return a previously sent value 
	 */
	def guardedRecv: Option[Any] = {
		synchronized {
			if (!blocked && writer != null && writer != Thread.currentThread) {
				if (!empty) return Some(recv)
				else reader = Thread.currentThread
			}
		}
		return None
	}

}
