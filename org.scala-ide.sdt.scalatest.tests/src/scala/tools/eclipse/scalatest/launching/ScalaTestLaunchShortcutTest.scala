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

import org.junit.Test
import org.junit.Assert._
import org.scalaide.core.internal.jdt.model.ScalaSourceFile
import org.junit.Ignore

class ScalaTestLaunchShortcutTest {
  
  // Tests marked with @Ignore requires jar file for specs1 and scalachecks wrapper runner, 
  // which is not in any public maven repo yet.  We could enable them back 
  // when they are in public maven repo.
  
  import ScalaTestProject._
  
  @Test
  def testSingleSpecIsScalaTestSuite() {
    val singleSpecFile = scalaCompilationUnit("com/test/SingleSpec.scala")
    singleSpecFile.getAllTypes.foreach { t =>
      assertTrue(t.getFullyQualifiedName + " expected as a ScalaTest suite, but it is not.", ScalaTestLaunchShortcut.isScalaTestSuite(t))
    }
  }
  
  @Test
  def testMultiSpecIsScalaTestSuite() {
    val multiSpecFile = scalaCompilationUnit("com/test/MultiSpec.scala")
    multiSpecFile.getAllTypes.foreach { t =>
      if (t.getFullyQualifiedName == "com.test.Fraction")
        assertFalse(t.getFullyQualifiedName + " expected as not a ScalaTest suite, but it is.", ScalaTestLaunchShortcut.isScalaTestSuite(t))
      else
        assertTrue(t.getFullyQualifiedName + " expected as a ScalaTest suite, but it is not.", ScalaTestLaunchShortcut.isScalaTestSuite(t))
    }
  }
  
  @Ignore
  def testSpec1IsScalaTestSuite() {
    val exampleSpec1File = scalaCompilationUnit("com/test/ExampleSpec1.scala")
    exampleSpec1File.getAllTypes.foreach { t =>
      assertTrue(t.getFullyQualifiedName + " expected as a ScalaTest suite, but it is not.", ScalaTestLaunchShortcut.isScalaTestSuite(t))
    }
  }
  
  @Ignore
  def testScalaCheckIsScalaTestSuite() {
    val stringSpecificationFile = scalaCompilationUnit("com/test/StringSpecification.scala")
    stringSpecificationFile.getAllTypes.foreach { t =>
      assertTrue(t.getFullyQualifiedName + " expected as a ScalaTest suite, but it is not.", ScalaTestLaunchShortcut.isScalaTestSuite(t))
    }
  }
  
  @Test
  def testSingleSpecContainsScalaTestSuite() {
    val singleSpecFile = scalaCompilationUnit("com/test/SingleSpec.scala").asInstanceOf[ScalaSourceFile]
    assertTrue(ScalaTestLaunchShortcut.containsScalaTestSuite(singleSpecFile))
  }
  
  @Test
  def testMultiSpecContainsScalaTestSuite() {
    val multiSpecFile = scalaCompilationUnit("com/test/MultiSpec.scala").asInstanceOf[ScalaSourceFile]
    assertTrue(ScalaTestLaunchShortcut.containsScalaTestSuite(multiSpecFile))
  }
  
  @Ignore
  def testSpec1ContainsScalaTestSuite() {
    val exampleSpec1File = scalaCompilationUnit("com/test/ExampleSpec1.scala").asInstanceOf[ScalaSourceFile]
    assertTrue(ScalaTestLaunchShortcut.containsScalaTestSuite(exampleSpec1File))
  }
  
  @Ignore
  def testScalaCheckContainsScalaTestSuite() {
    val stringSpecificationFile = scalaCompilationUnit("com/test/StringSpecification.scala").asInstanceOf[ScalaSourceFile]
    assertTrue(ScalaTestLaunchShortcut.containsScalaTestSuite(stringSpecificationFile))
  }
  
  @Test
  def testGetClassElement() {
    val singleSpecFile = scalaCompilationUnit("com/test/SingleSpec.scala").asInstanceOf[ScalaSourceFile]
    val allTypes = singleSpecFile.getAllTypes
    assertEquals(1, allTypes.size)
    val singleSpecType = allTypes(0)
    val children = singleSpecType.getChildren
    children.foreach { c =>
      assertEquals("com.test.SingleSpec", ScalaTestLaunchShortcut.getClassElement(c).getFullyQualifiedName)
    }
  }
}