package scala.tools.eclipse.scalatest.launching

import scala.io.Source
import java.net.{URL, URLClassLoader}
import java.io.File

object ScalaTestLauncher {

  def main(args: Array[String]) {
    try {
      val classpath = Source.fromFile(args(0)).getLines()
      val urls = classpath.map { cp => new File(cp.toString).toURI.toURL }.toArray
      val loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader)

      Thread.currentThread().setContextClassLoader(loader)
      
      val runnerClass =  loader.loadClass("org.scalatest.tools.Runner")
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
