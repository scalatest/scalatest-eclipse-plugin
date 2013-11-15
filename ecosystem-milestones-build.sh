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

build scala-ide-4.0-2_11 http://download.scala-ide.org/sdk/e38/scala211/dev/site/ 2.11.0-M5 e38-scala211-4.0 http://download.eclipse.org/releases/juno/
git checkout kepler-nightly
build scala-ide-4.0 http://download.scala-ide.org/sdk/e38/scala210/dev/site/ 2.10.3 e38-scala210-4.0 http://download.eclipse.org/releases/juno/

cd ${MERGE_TOOL_DIR}
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e38-scala210-4.0 package
mvn -Drepo.dest=${TARGET_DIR}/combined -Drepo.source=file://${TARGET_DIR}/e38-scala211-4.0 package
