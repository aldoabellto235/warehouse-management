#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file to
# you under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.2.0
# ----------------------------------------------------------------------------

# If JAVA_HOME is set, use it; otherwise use java from PATH
if [ -n "$JAVA_HOME" ]; then
  JAVA_EXEC="$JAVA_HOME/bin/java"
else
  JAVA_EXEC="java"
fi

# Determine the script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_WRAPPER_JAR="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.properties"

# Download wrapper jar if not present
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  if command -v curl > /dev/null; then
    curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -o "$MAVEN_WRAPPER_JAR"
  elif command -v wget > /dev/null; then
    wget -q "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -O "$MAVEN_WRAPPER_JAR"
  fi
fi

exec "$JAVA_EXEC" \
  -classpath "$MAVEN_WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$SCRIPT_DIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
