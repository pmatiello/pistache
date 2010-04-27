/* 
 * Copyright (c) 2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Fake agent for testing purposes.
 */

package pistache.testing

import pistache.picalculus.PiObject
import pistache.picalculus.Agent
import pistache.picalculus.Prefix

class FakePiObject extends PiObject

class FakeAgent extends Agent

class FakePrefix extends Prefix

class FakePrefixAgent extends Agent with Prefix
