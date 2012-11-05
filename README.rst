ScalaTest support in Scala IDE
==============================

This project contains plugins for seamless support of `scalatest`_ in `Scala IDE`_.

*This is a work in progress. Please file `tickets`_ if you encounter problems.*

Building
--------

Maven is used to manage the build process.  You can build the project for Scala IDE nightly releases.

For Scala IDE 2.0.2 (stable) and 2.1 (milestones), we recommend you to install from update sites listed here:-

  2.0.2 for Scala 2.9 (Indigo)   - http://download.scala-ide.org/sdk/e37/scala29/stable/site/
  2.1-M2 for Scala 2.9 (Indigo)  - http://download.scala-ide.org/sdk/e37/scala29/dev/site/
  2.1-M2 for Scala 2.10 (Indigo) - http://download.scala-ide.org/sdk/e37/scala210/dev/site/
  2.1-M2 for Scala 2.9 (Juno)    - http://download.scala-ide.org/sdk/e38/scala29/dev/site/
  2.1-M2 for Scala 2.10 (Juno)   - http://download.scala-ide.org/sdk/e38/scala210/dev/site/

*To build for Scala IDE nightly for Scala 2.9 (Indigo), use

  $ mvn clean install -P scala-ide-master-scala-2.9

*To build for Scala IDE nightly for Scala 2.10 (Indigo), use

  $ mvn clean install -P scala-ide-master-scala-trunk

*To build for Scala IDE nightly for Scala 2.9 (Juno), use

  $ mvn clean install -P scala-ide-juno-master-scala-2.9

*To build for Scala IDE nightly for Scala 2.10 (Juno), use

  $ mvn clean install -P scala-ide-juno-master-scala-trunk

*To build for Scala IDE 2.0 nightly for Scala 2.9 (Indigo), use

  $ mvn clean install -P scala-ide-2.0.x-scala-2.9

.. _scalatest: http://scalatest.org
.. _Scala IDE: http://scala-ide.org
.. _tickets: http://scala-ide.org/docs/user/community.html
.. _scala-ide/scala-ide: http://github.com/scala-ide/scala-ide

Using ScalaTest Plugin in Scala IDE 2.0
===============================================

What is in this guide?
----------------------

This guide will show you how to use the ScalaTest plugin in Scala IDE 2.0/2.1.

Prerequisites
.............

*   `Eclipse`_ 3.7 (Indigo) or 3.8/4.2 (Juno) with Scala IDE for Scala 2.9/2.10 installed (http://scala-ide.org).

    Check the getting started page http://scala-ide.org/docs/user/gettingstarted.html page for instructions on how to install Scala IDE.

*   Basic knowledge of the Eclipse user interface is required (in this guide).

*   Basic knowledge of the Scala language is required (in this guide).

*   Basic knowledge of the ScalaTest is required (in this guide).

Using ScalaTest in a Scala project
----------------------------------

To use ScalaTest in your Scala project, you must include ScalaTest in Build path of your project.

You can use ScalaTest 1.x or the latest 2.0.M4 (recommended).  Using ScalaTest 2.0 enables the following:-

*   Test result view built in the eclipse workspace.
*   Running of selected specific test or scope.

When using ScalaTest 1.x, the GUI Runner provided by ScalaTest will be used instead of the built-in test result view.

*   Run using ScalaTest 2.0.M4

.. image:: http://www.scalatest.org/assets/images/eclipseScreenshot.png
       :alt: Run using ScalaTest 2.0.M4
       :width: 100%
       :target: http://www.scalatest.org/assets/images/eclipseScreenshot.png

*   Run using ScalaTest 1.x

.. image:: http://www.scalatest.org/assets/images/eclipseScreenshot18.png
       :alt: Run using ScalaTest 1.x
       :width: 100%
       :target: http://www.scalatest.org/assets/images/eclipseScreenshot18.png


Running a Selected Suite
------------------------

To run a selected suite, you can select the suite using 2 different ways:-

*   By choosing the suite source in an opened Scala source file within the editor.
*   By choosing the suite class from Project Explorer/Navigator/Outline view.

After you choose the target suite element, just right click and choose:-

  Run As -> ScalaTest - Suite

A Run Configuration with the suite name will be created automatically.

Running a Selected Test
-----------------------

To run a selected test, click on the target test element in the editor, right click and choose:-

  Run As -> ScalaTest - Test

A Run Configuration with the test name will be created automatically.

Running a Selected Scope
------------------------

To run a selected scope, click on the target scope element in the editor, right click and choose:-

  Run As -> ScalaTest - Test

A Run Configuration with the scope name will be created automatically.

Running All Suites in a Selected File
-------------------------------------

To run all ScalaTest suites in a selected file, you can select the file using 2 different ways:-

*   By choosing an opened Scala source file containing ScalaTest suite(s) in the editor.
*   By choosing the Scala source file containing ScalaTest suite(s)  from Project Explorer/Navigator.

After you choose the target Scala source file, just right click and choose:-

  Run As -> ScalaTest - File

All ScalaTest suites in the selected Scala source file will be run.

A Run Configuration with the file name will be created automatically.

Running All Suites in Selected Package
--------------------------------------

To run all ScalaTests suites in a package, you can right click on a package in Project Explorer and choose:-

  Run As -> ScalaTest Package

All ScalaTest suites in the selected package (not nested package) will be run.  To include ScalaTest suites in nested packages, you'll need to select the 'Include Nested' option in the Run Configuration.

A Run Configuration with the package name will be created automatically.

Run Configuration Types
-----------------------

*   Suite   - You specify Suite class name (mandatory) and test name(s) to run.  If no test name is specified, all test(s) in the suite will be run.
*   File    - You specify Suite file (mandatory) to run, all ScalaTest suites in the selected file will be run.
*   Package - You specify Package name (mandatory) and whether to include nested package, all ScalaTest suites in the selected package will be run.  If 'Include Nested' is selected, all ScalaTest suites in nested package will be run as well.
