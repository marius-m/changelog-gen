#!/bin/bash

set -x

INSTALLER_TYPE=${1}
INPUT=${2}
OUTPUT=${3}
JAR=${4}
VERSION=${5}
APP_ICON=${6}
EXTRA_BUNDLER_ARGUMENTS=${7}

$JAVA_HOME/bin/javapackager \
  -deploy  \
  -Bruntime=${JRE_HOME} \
  -srcdir ${INPUT} \
  -srcfiles ${JAR} \
  -outdir ${OUTPUT} \
  -outfile "pdf-map" \
  -appclass "lt.ito.pdfmap.MainKt" \
  -native ${INSTALLER_TYPE} \
  -name "pdf-map" \
  -title "pdf-map" \
  -v \
  -nosign \
  -BjvmOptions=-Xmx600m -BjvmOptions=-Xms128m \
  -Bicon=${APP_ICON} \
  -BappVersion=${VERSION} \
  $EXTRA_BUNDLER_ARGUMENTS
