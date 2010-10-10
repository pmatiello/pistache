package pistache.runner.threaded

/** A concrete implementation of links.
 */
protected class LinkImplementation {
    private var buffer: Any = null
    private var empty = true
    private var blocked = false
    private var writer: AnyRef = null
    private var reader: AnyRef = null
    private var lock: AnyRef = new Object

    /** Make the thread wait until the given condition is satisfied.
     *
     *  @param cond the condition 
     */
    private def waitUntil(cond: => Boolean) {
        while (!cond) { lock.wait }
    }

    /** Send (store) a value through the link.
     *
     *  @param value the value 
     */
    def send(value: Any): Boolean = {
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
        lock.synchronized {
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
        lock.synchronized {
            reader = Thread.currentThread
            waitUntil(!empty)
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
    def guardedRecv: Option[Any] = {
        lock.synchronized {
            if (!blocked && writer != null && writer != Thread.currentThread) {
                if (!empty) return Some(recv)
                else reader = Thread.currentThread
            }
        }
        return None
    }

}
