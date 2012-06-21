ScalaTest support in Scala IDE
==============================

This project contains plugins for seamless support of `scalatest`_ in `Scala IDE`_.

*This is a work in progress. Please file `tickets`_ if you encounter problems.*

building
--------

Maven is used to manage the build process. The default configuration build the project for Scala IDE 2.0.1 with Scala 2.9.2.

  $ mvn clean install

*Until the build process is merged in scala-ide trunk, some extra parameters are needed:*

  $ mvn clean install -Drepo.scala-ide=http://download.scala-ide.org/luc-scala-ide-1001056/site/ -P scala-ide-master-scala-2.9 

The available profiles are:

* **scala-ide-2.0-scala-2.9** (default): stable Scala IDE (2.0.1) - stable Scala (2.9.2) *not available right now, building is not possible with the current Scala IDE stable update site*
* **scala-ide-2.0.x-scala-2.9**: maintenance Scala IDE (2.0.x) - stable Scala (2.9.2)
* **scala-ide-master-scala-2.9**: master Scala IDE (2.1.x) - stable Scala (2.9.2)
* **scala-ide-master-scala-trunk**: master Scala IDE (2.1.x) - trunk Scala (2.10.x) *disabled right now. Need to find the right library versions*

.. _scalatest: http://scalatest.org
.. _Scala IDE: http://scala-ide.org
.. _tickets: http://scala-ide.org/docs/user/community.html
.. _scala-ide/scala-ide: http://github.com/scala-ide/scala-ide
