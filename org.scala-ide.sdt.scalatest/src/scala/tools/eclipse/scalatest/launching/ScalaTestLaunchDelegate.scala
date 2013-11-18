/*
 * SCALA LICENSE
 *
 * Copyright (C) 2011-2012 Artima, Inc. All rights reserved.
 *
 * This software was developed by Artima, Inc.
 *
 * Permission to use, copy, modify, and distribute this software in source
 * or binary form for any purpose with or without fee is hereby granted,
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the EPFL nor the names of its contributors
 *    may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package scala.tools.eclipse.scalatest.launching

import org.eclipse.jdt.launching.{AbstractJavaLaunchConfigurationDelegate, JavaRuntime,
	                                IRuntimeClasspathEntry, VMRunnerConfiguration, ExecutionArguments}
import scala.tools.eclipse.ScalaPlugin
import java.io.{File, FileWriter, PrintWriter}
import com.ibm.icu.text.MessageFormat
import org.eclipse.core.runtime.{Path, CoreException, IProgressMonitor, NullProgressMonitor}
import org.eclipse.debug.core.{ILaunch, ILaunchConfiguration}
import org.eclipse.jdt.internal.launching.LaunchingMessages
import scala.collection.JavaConversions._
import scala.collection.mutable
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants
import ScalaTestLaunchConstants._
import scala.tools.eclipse.javaelements.ScalaSourceFile
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.core.resources.ResourcesPlugin
import java.net.URL
import java.net.URLClassLoader
import scala.tools.eclipse.scalatest.ui.ScalaTestPlugin
import scala.tools.eclipse.scalatest.ui.Node
import scala.tools.eclipse.scalatest.ui.TestModel
import scala.tools.eclipse.scalatest.ui.TestStatus
import scala.annotation.tailrec
import org.eclipse.core.runtime.FileLocator
import org.eclipse.core.runtime.Platform

class ScalaTestLaunchDelegate extends AbstractJavaLaunchConfigurationDelegate {
  
  def launchScalaTest(configuration: ILaunchConfiguration, mode: String, launch: ILaunch, monitor0: IProgressMonitor, stArgs: String) {
    val monitor = if (monitor0 == null) new NullProgressMonitor() else monitor0
		
    monitor.beginTask(configuration.getName() + "...", 3)

    if (monitor.isCanceled())
      return
			
    try {
      monitor.subTask(LaunchingMessages.JavaLocalApplicationLaunchConfigurationDelegate_Verifying_launch_attributes____1) 
							
      val mainTypeName = "scala.tools.eclipse.scalatest.launching.ScalaTestLauncher"
      val runner = getVMRunner(configuration, mode)
	
      val workingDir = verifyWorkingDirectory(configuration)
      val workingDirName = if (workingDir != null) workingDir.getAbsolutePath() else null
			
      // Environment variables
      val envp = getEnvironment(configuration)
			
      // VM-specific attributes
      val vmAttributesMap = getVMSpecificAttributesMap(configuration)

      val modifiedAttrMap: mutable.Map[String, Array[String]] =
        if (vmAttributesMap == null) mutable.Map() else vmAttributesMap.asInstanceOf[java.util.Map[String,Array[String]]]
      val classpath0 = getClasspath(configuration)
      val missingScalaLibraries = toInclude(modifiedAttrMap,
					classpath0.toList, configuration)
      // Classpath
      // Add scala libraries that were missed in VM attributes
      val classpath = (classpath0.toList):::missingScalaLibraries
			
      // Program & VM arguments	
      val vmArgs = getVMArguments(configuration)
			
      val loaderUrls = classpath.map{ cp => new File(cp.toString).toURI.toURL }

      val bootClassPath = getBootpath(configuration)
      
      val bootClassLoader = new URLClassLoader(bootClassPath.map(new File(_).toURI.toURL))
      val loader:ClassLoader = new URLClassLoader(loaderUrls.toArray, bootClassLoader)
      
      val pgmArgs = 
      try {
        loader.loadClass("org.scalatest.tools.SocketReporter")
        ScalaTestPlugin.asyncShowTestRunnerViewPart(launch, configuration.getName, configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""))
        val port = ScalaTestPlugin.listener.getPort
        getProgramArguments(configuration) + " " + stArgs + " -oW -k localhost " + port
      }
      catch {
        case e: Throwable => 
          getProgramArguments(configuration) + " " + stArgs + " -oW -g"
      }
      
      // In 2.11, XML classes has been modularized into its own jar, and by default Scala IDE does not include it.
      // We'll include it automatically in boot class path so that our XmlSocketReporter still works.
      val missingScalaXml = 
        try {
          loader.loadClass("scala.xml.Elem")
          List.empty[String]
        }
        catch {
          case e: Throwable => 
            val codeSource = Class.forName("scala.xml.Elem").getProtectionDomain.getCodeSource
            if (codeSource != null) // code source could be null
              List(codeSource.getLocation.toExternalForm)
            else
              List.empty[String]
        }
            
      val execArgs = new ExecutionArguments(vmArgs, pgmArgs)
      
      val bundle = Platform.getBundle("org.scala-ide.sdt.scalatest")
      val bundleFile = FileLocator.getBundleFile(bundle)
      val bundlePath = 
        if (bundleFile.isDirectory) {
          val targetClasses = new File(bundleFile, "target/classes")
          if (targetClasses.exists)
            targetClasses.getCanonicalPath
          else
            bundleFile.getCanonicalPath
        }
        else
          bundleFile.getCanonicalPath
      
      // Create classpath file
      val cpFile = File.createTempFile("st-launch", ".classpath")
      val cpFileOut = new PrintWriter(new FileWriter(cpFile))
      (classpath ::: missingScalaXml).map{ cp => cpFileOut.println(new File(cp.toString).getCanonicalPath) }
      cpFileOut.flush()
      cpFileOut.close()
      
      // Create args file
      val argsFile = File.createTempFile("st-launch", ".args")
      val argsFileOut = new PrintWriter(new FileWriter(argsFile))
      execArgs.getProgramArgumentsArray().foreach(arg => argsFileOut.println(arg))
      argsFileOut.flush()
      argsFileOut.close()
      
      // Create VM config
      //val runConfig = new VMRunnerConfiguration(mainTypeName, (classpath ::: missingScalaXml).toArray)
      val runConfig = new VMRunnerConfiguration(mainTypeName, Array(bundlePath))
      //runConfig.setProgramArguments(execArgs.getProgramArgumentsArray())
      runConfig.setProgramArguments(Array(cpFile.getAbsolutePath, argsFile.getAbsolutePath))
      runConfig.setEnvironment(envp)
      runConfig.setVMArguments(execArgs.getVMArgumentsArray())
      runConfig.setWorkingDirectory(workingDirName)
      runConfig.setVMSpecificAttributesMap(vmAttributesMap)

      // Bootpath
      runConfig.setBootClassPath(bootClassPath)
			
      // check for cancellation
      if (monitor.isCanceled())
        return
			
      // stop in main
      prepareStopInMain(configuration)
			
      // done the verification phase
      monitor.worked(1)
			
      monitor.subTask(LaunchingMessages.JavaLocalApplicationLaunchConfigurationDelegate_Creating_source_locator____2) 
      // set the default source locator if required
      setDefaultSourceLocator(launch, configuration)
      monitor.worked(1)
			
      // Launch the configuration - 1 unit of work
      runner.run(runConfig, launch, monitor)
			
      // check for cancellation
      if (monitor.isCanceled())
        return
      }
      finally {
        monitor.done()
      }
  }
  
  def launch(configuration: ILaunchConfiguration, mode: String, launch: ILaunch, monitor0: IProgressMonitor) {
    // Test Class
    val stArgs = getScalaTestArgs(configuration)
    launchScalaTest(configuration, mode, launch, monitor0, stArgs)
  }
	
  private def toInclude(vmMap: mutable.Map[String, Array[String]], classpath: List[String],
			          configuration: ILaunchConfiguration): List[String] =
    missingScalaLibraries((vmMap.values.flatten.toList) ::: classpath, configuration)

  private def missingScalaLibraries(included: List[String], configuration: ILaunchConfiguration): List[String] =  {
    val entries = JavaRuntime.computeUnresolvedRuntimeClasspath(configuration).toList
    val libid = Path.fromPortableString(ScalaPlugin.plugin.scalaLibId)
    val found = entries.find(e => e.getClasspathEntry != null && e.getClasspathEntry.getPath == libid)
    found match {
      case Some(e) =>
        val scalaLibs = resolveClasspath(e, configuration)
        scalaLibs.diff(included)
      case None =>
        List()
    }
  }
	
  private def resolveClasspath(a: IRuntimeClasspathEntry, configuration: ILaunchConfiguration): List[String] = {
    val bootEntry = JavaRuntime.resolveRuntimeClasspath(Array(a), configuration)
    bootEntry.toList.map(_.getLocation())
  }
  
  // TODO Needs an expert's review
  private[launching] def escapeScalaTestClasspathComponent(comp: String): String = {
    require(comp != null)
    comp.replaceAll("""\s""", """\\ """).replaceAll(""""""", """\\"""")
  }
  
  private[launching] def getScalaTestArgs(configuration: ILaunchConfiguration): String = {
    require(configuration != null)
    
    val launchType = configuration.getAttribute(SCALATEST_LAUNCH_TYPE_NAME, TYPE_SUITE)
    val outputDir = getClasspath(configuration).foldLeft("")((acc, act) => acc + " " + escapeScalaTestClasspathComponent(act)).trim
    launchType match {
      case TYPE_SUITE => 
        val suiteClass = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "")
        val testSet: java.util.Set[String] = configuration.getAttribute(SCALATEST_LAUNCH_TESTS_NAME, new java.util.HashSet[String]()).asInstanceOf[java.util.Set[String]]
        if (testSet.size == 0) 
          "-p \"" + outputDir + "\" -s " + suiteClass
        else
          "-p \"" + outputDir + "\" -s " + suiteClass + " " + testSet.map("-t \"" + _ + "\"").mkString(" ")
      case TYPE_FILE =>
        val filePortablePath = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "")
        if (filePortablePath.length > 0) {
          val scSrcFileOpt = ScalaSourceFile.createFromPath(filePortablePath)
          scSrcFileOpt match {
            case Some(scSrcFile) => 
              "-p \"" + outputDir + "\" " + 
              scSrcFile.getTypes
                .filter(ScalaTestLaunchShortcut.isScalaTestSuite(_))
                .map(iType => "-s " + iType.getFullyQualifiedName)
                .mkString(" ")
            case None => 
              MessageDialog.openError(null, "Error", "File '" + filePortablePath + "' not found.")
              ""
          }
        }
        else
          ""
      case TYPE_PACKAGE =>
        val packageName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "")
        if (packageName.length > 0) {
          val includeNested = configuration.getAttribute(SCALATEST_LAUNCH_INCLUDE_NESTED_NAME, INCLUDE_NESTED_FALSE)
          if (includeNested == INCLUDE_NESTED_TRUE) 
            "-p \"" + outputDir + "\" -w " + packageName
          else
            "-p \"" + outputDir + "\" -m " + packageName
        }
        else
          ""
      case _ =>
        ""
    }
  }
  
  def getScalaTestArgsForSuite(suiteClassName: String, suiteId: String) = {
    if (suiteClassName == suiteId)
      "-s " + suiteClassName
    else
      "-s " + suiteClassName + " -i \"" + suiteId + "\""
  }
  
  def getScalaTestArgsForTest(suiteClassName: String, suiteId: String, testName: String) = {
    if (suiteClassName == suiteId)
      "-s " + suiteClassName + " -t \"" + testName + "\""
    else
      "-s " + suiteClassName + " -i \"" + suiteId + "\" -t \"" + testName + "\""
  }
  
  def getScalaTestArgsForFailedTests(node: Node) = {
    @tailrec
    def getFailedTestsAcc(acc: List[TestModel], children: List[Node]): List[TestModel] = {
      children match {
        case Nil => 
          acc
        case head :: rest => 
          val newAcc = head match {
            case test: TestModel if test.status == TestStatus.FAILED => 
              test :: acc
            case _ =>
              acc
          }
          getFailedTestsAcc(newAcc, rest ++ head.children)
      }
    }
    val failedTests = getFailedTestsAcc(Nil, List(node))
    failedTests.map { test => 
      test.rerunner match {
        case Some(rerunner) => 
          getScalaTestArgsForTest(rerunner, test.suiteId, test.testName)
        case None => 
          ""
      }
    }.filter(_ != "").mkString(" ")
  }
}