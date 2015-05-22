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

import org.eclipse.debug.core.DebugPlugin
import org.eclipse.debug.core.ILaunchManager
import org.junit.Ignore
import org.junit.Test

import ScalaTestProject.file

class ScalaTestLaunchTest {

  // Tests marked with @Ignore requires jar file for specs1 and scalachecks wrapper runner,
  // which is not in any public maven repo yet.  We could enable them back
  // when they are in public maven repo.

  import ScalaTestProject._

  private def launch(launchName: String, mode: String = ILaunchManager.RUN_MODE) {
    val launchConfig = DebugPlugin.getDefault.getLaunchManager.getLaunchConfiguration(file(launchName + ".launch"))
    launchConfig.launch(mode, null)
  }

  @Test
  def testLaunchComTestPackage() {
    launch("com.test")
  }

  @Test
  def testLaunchSingleSpecFile() {
    launch("SingleSpec.scala")
  }

  @Test
  def testLaunchMultiSpecFile() {
    launch("MultiSpec.scala")
  }

  @Test
  def testLaunchSingleSpec() {
    launch("SingleSpec")
  }

  @Test
  def testLaunchStackSpec2() {
    launch("StackSpec2")
  }

  @Test
  def testLaunchTestingFreeSpec() {
    launch("TestingFreeSpec")
  }

  @Test
  def testLaunchTestingFunSuite() {
    launch("TestingFunSuite")
  }

  @Test
  def testLaunchConfigAStackshouldtastelikepeanutbutter() {
    launch("AStackshouldtastelikepeanutbutter")
  }

  @Test
  def testLaunchConfigAStackwhenemptyshouldcomplainonpop() {
    launch("AStackwhenemptyshouldcomplainonpop")
  }

  @Test
  def testLaunchConfigAStackwhenfull() {
    launch("AStackwhenfull")
  }

  @Test
  def testLaunchConfigAStackwheneveritisemptycertainlyoughttocomplainonpeek() {
    launch("AStackwheneveritisemptycertainlyoughttocomplainonpeek")
  }

  @Test
  def testLaunchConfigAStackwheneveritisempty() {
    launch("AStackwheneveritisempty")
  }

  @Test
  def testLaunchConfigAStack() {
    launch("AStack")
  }

  @Test
  def `testLaunchConfigcom.test.TestingFunSuite-'test2'`() {
    launch("com.test.TestingFunSuite-'test2'")
  }

  @Test
  def testLaunchComTestPackageWithScalaRunner() {
    launch("com.test.scalarunner")
  }

  @Test
  def testLaunchComTestPackageWithScalaDebugger() {
    launch("com.test.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchSingleSpecFileWithScalaRunner() {
    launch("SingleSpec.scala.scalarunner")
  }

  @Test
  def testLaunchSingleSpecFileWithScalaDebugger() {
    launch("SingleSpec.scala.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchMultiSpecFileWithScalaRunner() {
    launch("MultiSpec.scala.scalarunner")
  }

  @Test
  def testLaunchMultiSpecFileWithScalaDebugger() {
    launch("MultiSpec.scala.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchSingleSpecWithScalaRunner() {
    launch("SingleSpec.scalarunner")
  }

  @Test
  def testLaunchSingleSpecWithScalaDebugger() {
    launch("SingleSpec.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchStackSpec2WithScalaRunner() {
    launch("StackSpec2.scalarunner")
  }

  @Test
  def testLaunchStackSpec2WithScalaDebugger() {
    launch("StackSpec2.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchTestingFreeSpecWithScalaRunner() {
    launch("TestingFreeSpec.scalarunner")
  }

  @Test
  def testLaunchTestingFreeSpecWithScalaDebugger() {
    launch("TestingFreeSpec.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchTestingFunSuiteWithScalaRunner() {
    launch("TestingFunSuite.scalarunner")
  }

  @Test
  def testLaunchTestingFunSuiteWithScalaDebugger() {
    launch("TestingFunSuite.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchConfigAStackshouldtastelikepeanutbutterWithScalaRunner() {
    launch("AStackshouldtastelikepeanutbutter.scalarunner")
  }

  @Test
  def testLaunchConfigAStackshouldtastelikepeanutbutterWithScalaDebugger() {
    launch("AStackshouldtastelikepeanutbutter.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchConfigAStackwhenemptyshouldcomplainonpopWithScalaRunner() {
    launch("AStackwhenemptyshouldcomplainonpop.scalarunner")
  }

  @Test
  def testLaunchConfigAStackwhenemptyshouldcomplainonpopWithScalaDebugger() {
    launch("AStackwhenemptyshouldcomplainonpop.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchConfigAStackwhenfullWithScalaRunner() {
    launch("AStackwhenfull.scalarunner")
  }

  @Test
  def testLaunchConfigAStackwhenfullWithScalaDebugger() {
    launch("AStackwhenfull.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchConfigAStackwheneveritisemptycertainlyoughttocomplainonpeekWithScalaRunner() {
    launch("AStackwheneveritisemptycertainlyoughttocomplainonpeek.scalarunner")
  }

  @Test
  def testLaunchConfigAStackwheneveritisemptycertainlyoughttocomplainonpeekWithScalaDebugger() {
    launch("AStackwheneveritisemptycertainlyoughttocomplainonpeek.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchConfigAStackwheneveritisemptyWithScalaRunner() {
    launch("AStackwheneveritisempty.scalarunner")
  }

  @Test
  def testLaunchConfigAStackwheneveritisemptyWithScalaDebugger() {
    launch("AStackwheneveritisempty.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def testLaunchConfigAStackWithScalaRunner() {
    launch("AStack.scalarunner")
  }

  @Test
  def testLaunchConfigAStackWithScalaDebugger() {
    launch("AStack.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Test
  def `testLaunchConfigcom.test.TestingFunSuite-'test2'WithScalaRunner`() {
    launch("com.test.TestingFunSuite-'test2'.scalarunner")
  }

  @Test
  def `testLaunchConfigcom.test.TestingFunSuite-'test2'WithScalaDebugger`() {
    launch("com.test.TestingFunSuite-'test2'.scalarunner", ILaunchManager.DEBUG_MODE)
  }

  @Ignore
  def testLaunchExampleSpec1File() {
    launch("ExampleSpec1.scala")
  }

  @Ignore
  def testLaunchExampleSpec1() {
    launch("ExampleSpec1")
  }

  @Ignore
  def testLaunchConfigMysystem() {
    launch("Mysystem")
  }

  @Ignore
  def testLaunchConfigMysystemalsocanprovidesadvancedfeature1() {
    launch("Mysystemalsocanprovidesadvancedfeature1")
  }

  @Ignore
  def testLaunchStringSpecificationFile() {
    launch("StringSpecification.scala")
  }

  @Ignore
  def testLaunchSpringSpecification() {
    launch("StringSpecification")
  }

  @Ignore
  def `testLaunchConfigcom.test.StringSpecification-'substring1'`() {
    launch("com.test.StringSpecification-'substring1'")
  }
}