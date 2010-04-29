package pistache.picalculus

object Implicits {

	implicit def PiObjectToAgent(piObject: PiObject) = PrefixAgent(piObject)
  
}

/** A class representing pi-Calculus prefixes as agents.
 * 
 *  @param agent the agent.
 */
case class PrefixAgent(val agent: PiObject) extends Agent