package scala.tools.eclipse.scalatest.launching

import org.scalaide.debug.internal.launching.ScalaDebuggerForLaunchDelegate

class ScalaTestScalaLaunchDelegate extends ScalaTestLaunchDelegate with ScalaDebuggerForLaunchDelegate {
  override protected def addToVmRunnerClasspath(classpath: Seq[String]): Array[String] = classpath.toArray
}