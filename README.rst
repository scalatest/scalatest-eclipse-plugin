scalatest support in Scala IDE
==============================

This project contains plugins for seamless support of `scalatest`__ in `Scala IDE`__.

__ http://scalatest.org
__ http://scala-ide.org

building
--------

Maven is used to manage the build process. The default configuration build the project for Scala IDE 2.0.x with Scala 2.9.x.

  $ maven clean install

Two other profiles are provided: scala-ide-master-scala-2.9 and scala-ide-master-scala-trunk.

  $ maven clean install -P scala-ide-master-scala-trunk
