#!/usr/bin/env bash

# This script loads spark-env.sh if it exists, and ensures it is only loaded once.
# spark-env.sh is loaded from SDOOB_CONF_DIR if set, or within the current directory's
# conf/ subdirectory.

SDOOB_ENV_SH="spark-env.sh"

if [ -z "$SPARK_ENV_LOADED" ]; then
  export SPARK_ENV_LOADED=1

  export SDOOB_CONF_DIR="${SDOOB_CONF_DIR:-"${PROG_HOME}"/conf}"

  SPARK_ENV_SH="${SDOOB_CONF_DIR}/${SDOOB_ENV_SH}"
  if [[ -f "${SPARK_ENV_SH}" ]]; then
    # Promote all variable declarations to environment (exported) variables
    set -a
    . ${SPARK_ENV_SH}
    set +a
  fi
fi

PSEP=":"

# For Cygwin, switch paths to Windows-mixed format before running java
if $cygwin; then
  PSEP=";"
fi

# For Migwn, ensure paths are in UNIX format before anything is touched
if $mingw ; then
  PSEP=";"
fi

LAUNCH_CLASSPATH="${SDOOB_CONF_DIR}/"

if [ $SPARK_HOME ]; then
  LAUNCH_CLASSPATH="${LAUNCH_CLASSPATH}${PSEP}${SPARK_HOME}/jars/*"
fi

if [ $HADOOP_CONF_DIR ]; then
  LAUNCH_CLASSPATH="${LAUNCH_CLASSPATH}${PSEP}${HADOOP_CONF_DIR}/"
fi

if [ $YARN_CONF_DIR ]; then
  LAUNCH_CLASSPATH="${LAUNCH_CLASSPATH}${PSEP}${YARN_CONF_DIR}/"
fi

export SPARK_CLASSPATH="${LAUNCH_CLASSPATH}${PSEP}${SPARK_CLASSPATH}"
echo "SPARK_CLASSPATH is $SPARK_CLASSPATH"

