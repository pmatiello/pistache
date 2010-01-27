/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec runner.
 */

package pistache.testing;

import pistache.integration._
import pistache.picalculus._
import pistache.runner._

object SpecRunner extends Application {

	new ActionSpec execute
  
	new IfSpec execute
   
	new IntegrationTests execute
  
	new LinkSpec execute
    
	new NameSpec execute
    
	new ProcessSpec execute
   
	new ThreadedRunnerSpec execute
   
   
  
}
