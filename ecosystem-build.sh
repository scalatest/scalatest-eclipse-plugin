#!/bin/bash


####################
#
# $1 - build profile
# $2 - ecosystem location
# $3 - scala version 
# $4 - ecosystem id
# $5 - eclipse ecosystem
#
####################
function build() {
  cd ${ROOT_DIR}
  mvn -Pset-versions -P$1 -Drepo.scala-ide=$2 -Dscala.version=$3 -Drepo.eclipse=$5 -Dtycho.style=maven --non-recursive exec:java

  mvn -Pset-versions -P$1 -Drepo.scala-ide=$2 -Dscala.version=$3 -Drepo.eclipse=$5 clean package

  rm -rf ${TARGET_DIR}/$4
  mkdir -p ${TARGET_DIR}

  cp -r ${ROOT_DIR}/org.scala-ide.sdt.scalatest.update-site/target/site/ ${TARGET_DIR}/$4
}

###################

# root dir (containing this script)
ROOT_DIR=$(dirname $0)
cd ${ROOT_DIR}
ROOT_DIR=${PWD}

TARGET_DIR=~/tmp/scalatest-build-ecosystem

# scala-ide/build-tools/maven-tool/merge-site/ location
MERGE_TOOL_DIR=~/git/build-tools/maven-tool/merge-site

###################

set -x

rm -rf ${TARGET_DIR}

build scala-ide-3.0-2_10 http://download.scala-ide.org/sdk/helium/e38/scala210/stable/site/ 2.10.4 e38-scala210-3.0 http://download.eclipse.org/releases/juno/
build scala-ide-3.0-2_10 http://download.scala-ide.org/sdk/helium/e37/scala210/stable/site/ 2.10.2 e37-scala210-3.0 http://download.eclipse.org/releases/indigo/
build scala-ide-3.0 http://download.scala-ide.org/sdk/helium/e38/scala29/stable/site/ 2.9.3-SNAPSHOT e38-scala29-3.0 http://download.eclipse.org/releases/juno/
build scala-ide-3.0 http://download.scala-ide.org/sdk/helium/e37/scala29/stable/site/ 2.9.3-SNAPSHOT e37-scala29-3.0 http://download.eclipse.org/releases/indigo/
git checkout .
rm org.scala-ide.sdt.scalatest.feature/feature.xml.original
rm org.scala-ide.sdt.scalatest.source.feature/feature.xml.original
rm org.scala-ide.sdt.scalatest.tests/META-INF/MANIFEST.MF.original
rm org.scala-ide.sdt.scalatest/META-INF/MANIFEST.MF.original
git checkout 2.11
build scala-ide-3.0-2_11 http://download.scala-ide.org/sdk/next/helium/e38/scala211/stable/site/ 2.11.1 e38-scala211-3.0 http://download.eclipse.org/releases/juno/
git checkout .
rm org.scala-ide.sdt.scalatest.feature/feature.xml.original
rm org.scala-ide.sdt.scalatest.source.feature/feature.xml.original
rm org.scala-ide.sdt.scalatest.tests/META-INF/MANIFEST.MF.original
rm org.scala-ide.sdt.scalatest/META-INF/MANIFEST.MF.original
git checkout kepler-nightly
build scala-ide-4.0 http://download.scala-ide.org/sdk/lithium/e38/scala210/dev/site/ 2.10.4 e38-scala210-4.0 http://download.eclipse.org/releases/juno/
git checkout .
rm org.scala-ide.sdt.scalatest.feature/feature.xml.original
rm org.scala-ide.sdt.scalatest.source.feature/feature.xml.original
rm org.scala-ide.sdt.scalatest.tests/META-INF/MANIFEST.MF.original
rm org.scala-ide.sdt.scalatest/META-INF/MANIFEST.MF.original
git checkout kepler-nightly-2.11
build scala-ide-4.0-2_11 http://download.scala-ide.org/sdk/lithium/e38/scala211/dev/site/ 2.11.1 e38-scala211-4.0 http://download.eclipse.org/releases/juno/
build scala-ide-4.0-2_11 http://download.scala-ide.org/sdk/lithium/e44/scala211/dev/site/ 2.11.1 e44-scala211-4.0 http://download.eclipse.org/releases/juno/

cd ${MERGE_TOOL_DIR}
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e37-scala29-3.0 package
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e38-scala29-3.0 package
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e37-scala210-3.0 package
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e38-scala210-3.0 package
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e38-scala210-4.0 package
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e38-scala211-3.0 package
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e38-scala211-4.0 package
