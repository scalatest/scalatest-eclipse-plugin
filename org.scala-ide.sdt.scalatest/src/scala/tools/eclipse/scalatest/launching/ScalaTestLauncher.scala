package scala.tools.eclipse.scalatest.launching

import scala.io.Source
import java.net.{URL, URLClassLoader}
import java.io.File

object ScalaTestLauncher {

  def main(args: Array[String]) {
    try {
      val cpFilePath = args(0)
      val classpath = Source.fromFile(args(0)).getLines()
    
      val loader = ClassLoader.getSystemClassLoader
      val method= classOf[URLClassLoader].getDeclaredMethod("addURL", classOf[URL]); //$NON-NLS-1$
      method.setAccessible(true);
      classpath.foreach(cp => method.invoke(loader, new File(cp.toString).toURI.toURL))
    
      val runnerClass = Class.forName("org.scalatest.tools.Runner")
      val mainMethod = runnerClass.getMethod("main", args.getClass()) //$NON-NLS-1$
      mainMethod.setAccessible(true)
      mainMethod.invoke(null, Source.fromFile(args(1)).getLines().toArray)
    }
    catch {
      case e: Throwable => e.printStackTrace()
    }
    finally {
      val cpFile = new File(args(0))
      cpFile.delete()
      val argsFile = new File(args(1))
      argsFile.delete()
    }
  }
  
}