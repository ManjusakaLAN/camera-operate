#!/bin/bash

# 设置Maven仓库路径  如果没设置就设置为 /root/maven-repo
LOCAL_REPOSITORY=${LOCAL_REPOSITORY:-/root/maven-repo}

# 指定 Jar包位置
JAR_LOCATION=/root/jna.jar

# 安装依赖
mvn install:install-file \
  -Dmaven.repo.local=$LOCAL_REPOSITORY \
  -Dfile=$JAR_LOCATION \
  -DgroupId=net.java.hk.jna \
  -DartifactId=hkjna \
  -Dversion=1.0.0 \
  -Dpackaging=jar

# 指定 Jar包位置
JAR_LOCATION=/root/examples.jar

# 安装依赖
mvn install:install-file \
  -Dmaven.repo.local=$LOCAL_REPOSITORY \
  -Dfile=$JAR_LOCATION \
  -DgroupId=net.java.examples \
  -DartifactId=examples \
  -Dversion=1.0.0 \
  -Dpackaging=jar
