package pistache.picalculus

object If {
  
	def apply(condition: => Boolean)(process: Process) = new IfProcess(condition, process)

}

protected class IfProcess(cond: => Boolean, process:Process) extends Process {
	
	val condition = cond _
	val description = process
}