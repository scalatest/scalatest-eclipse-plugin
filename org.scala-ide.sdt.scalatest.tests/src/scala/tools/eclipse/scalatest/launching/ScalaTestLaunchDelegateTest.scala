package scala.tools.eclipse.scalatest.launching

import org.junit.Test
import org.junit.Assert._
import org.mockito.Mockito._
import org.eclipse.debug.core.ILaunchConfiguration
import ScalaTestLaunchConstants._
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants
import org.eclipse.debug.internal.core.LaunchConfiguration
import org.mockito.Matchers
import org.mockito.Mockito
import org.junit.runner.RunWith

/**
 * Unit tests for ScalaTestLaunchDelegateTest.
 *
 * @author rlegendi
 * @author cheeseng
 */
class ScalaTestLaunchDelegateTest {

  // --------------------------------------------------------------------------
  // Escaping test

  @Test(expected = classOf[IllegalArgumentException])
  def testEscapingForNullClassPathEntry() {
    // TODO These might be static methods in a companion object
    def delegate = new ScalaTestLaunchDelegate
    delegate.escapeScalaTestClasspathComponent(null)
  }

  @Test
  def testEscapingForEmptyClassPathEntry() {
    def delegate = new ScalaTestLaunchDelegate
    def res = delegate.escapeScalaTestClasspathComponent("")
    assertEquals("", res)
  }

  @Test
  def testEscapingForSimpleClassPathEntry() {
    def delegate = new ScalaTestLaunchDelegate
    def res = delegate.escapeScalaTestClasspathComponent("some.jar")
    assertEquals("some.jar", res)
  }

  @Test
  def testEscapingForClassPathEntryWithSingleSpace() {
    def delegate = new ScalaTestLaunchDelegate

    def res = delegate.escapeScalaTestClasspathComponent("Program files")
    assertEquals("Program\\ files", res)
  }

  @Test
  def testEscapingForClassPathEntryWithDoubleSpaces() {
    def delegate = new ScalaTestLaunchDelegate

    def res = delegate.escapeScalaTestClasspathComponent("Program  files")
    assertEquals("Program\\ \\ files", res)
  }

  @Test
  def testEscapingForClassPathEntryWithDoubleQuotes() {
    def delegate = new ScalaTestLaunchDelegate

    def res = delegate.escapeScalaTestClasspathComponent("middle\"escaped")
    assertEquals("middle\\\"escaped", res)
  }

  // --------------------------------------------------------------------------
  // ScalaTest argument building test

  @Test(expected = classOf[IllegalArgumentException])
  def argsBuildingForNull() {
    def delegate = new ScalaTestLaunchDelegate
    delegate.getScalaTestArgs(null)
  }

  private def evalScalaTestArgs(cps: String*): String = {
    val config = mock(classOf[ILaunchConfiguration])

    when(config.getAttribute(SCALATEST_LAUNCH_TYPE_NAME, TYPE_SUITE)).thenReturn(TYPE_PACKAGE)
    when(config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "")).thenReturn("testpkg")
    when(config.getAttribute(SCALATEST_LAUNCH_INCLUDE_NESTED_NAME, INCLUDE_NESTED_FALSE)).thenReturn(INCLUDE_NESTED_TRUE)

    val delegate = new ScalaTestLaunchDelegate() {
      override def getClasspath(configuration: ILaunchConfiguration): Array[String] = cps.toArray
    }

    delegate.getScalaTestArgs(config)
  }

  @Test
  def argsBuildingForEmptyClassPath() {
    val res = evalScalaTestArgs("")
    assertEquals("-p \"\" -w testpkg", res)
  }

  @Test
  def argsBuildingForSingleClassPath() {
    val res = evalScalaTestArgs("some.jar")
    assertEquals("-p \"some.jar\" -w testpkg", res)
  }

  @Test
  def argsBuildingForMultipleSimpleClassPath() {
    val res = evalScalaTestArgs("some.jar", "other.jar")
    assertEquals("-p \"some.jar other.jar\" -w testpkg", res)
  }

  @Test
  def argsBuildingForFolderAndJarClassPath() {
    val res = evalScalaTestArgs("some/folder", "and/", "some.jar")
    assertEquals("-p \"some/folder and/ some.jar\" -w testpkg", res)
  }

  @Test
  def argsBuildingForSingleSpacedClassPath() {
    val res = evalScalaTestArgs("some.jar", "and a spaced folder", "andSomething/else")
    assertEquals("-p \"some.jar and\\ a\\ spaced\\ folder andSomething/else\" -w testpkg", res)
  }

}
