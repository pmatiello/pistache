/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec runner.
 */

package pistache.testing;

import pistache.integration._
import pistache.picalculus._
import pistache.runner.threaded._

object SpecRunner {

    def main(args: Array[String]) {

        new ActionSpec execute

        new AgentSpec execute

        new IfSpec execute

        new LinkSpec execute

        new NameSpec execute

        new PrefixSpec execute

        new ThreadedRunnerSpec execute

        new IntegrationTests execute
        
    }

}
