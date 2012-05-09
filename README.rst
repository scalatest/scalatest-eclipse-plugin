ScalaTest support in Scala IDE
==============================

This project contains plugins for seamless support of `scalatest`__ in `Scala IDE`__.

__ http://scalatest.org
__ http://scala-ide.org

building
--------

Pre-requisite
.............

To successfully build this project, org.scala-ide:scala-ide-for-eclipse:1 is required, but it is not publish.

To install it locally, clone `scala-ide/scala-ide`__ and run ``mvn install`` from the ``org.scala-ide`` folder.

__ http://github.com/scala-ide/scala-ide

building the project
....................

Maven is used to manage the build process. The default configuration build the project for Scala IDE 2.0.x with Scala 2.9.x.

  $ maven clean install

Two other profiles are provided: scala-ide-master-scala-2.9 and scala-ide-master-scala-trunk.

  $ maven clean install -P scala-ide-master-scala-trunk
