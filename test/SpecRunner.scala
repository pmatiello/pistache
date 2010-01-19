/* 
 * Copyright (c) 2009-2010 Pedro Matiello <pmatiello@gmail.com>
 * 
 * Spec runner.
 */

import pistache.picalculus._
import pistache.runner._

object SpecRunner extends Application {

	new IfSpec execute
  
	new LinkSpec execute
    
	new NameSpec execute
    
	new ProcessSpec execute
   
	new ActionSpec execute
   
	new ThreadedRunnerSpec execute
  
}
