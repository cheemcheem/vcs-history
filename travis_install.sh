#!/usr/bin/env sh
if [ ! -d "/opt/gradle/gradle-5.0/bin" ]; then
  echo "Installing Gradle 5."
  wget https://services.gradle.org/distributions/gradle-5.0-bin.zip -P /tmp
  sudo unzip -d /opt/gradle /tmp/gradle-*.zip
  ls /opt/gradle/gradle-5.0
  export GRADLE_HOME=/opt/gradle/gradle-5.0
  export PATH=${GRADLE_HOME}/bin:${PATH}
  gradle -v
else
  echo "Gradle 5 already installed."
fi