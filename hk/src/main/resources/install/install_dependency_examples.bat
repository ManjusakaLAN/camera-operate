@echo off

:: 设置本地仓库路径
set LOCAL_REPOSITORY=E:\apache-maven-3.9.4-bin\maven-repository

:: JAR文件路径
set JAR_LOCATION=examples.jar

:: 检查文件是否存在
if not exist %JAR_LOCATION% (
  echo File %JAR_LOCATION% does not exist.
  goto :eof
)

:: 安装Examples依赖
echo Executing: mvn install:install-file -Dmaven.repo.local=%LOCAL_REPOSITORY% -Dfile=%JAR_LOCATION% -DgroupId=net.java.examples -DartifactId=examples -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file ^
  -Dmaven.repo.local=%LOCAL_REPOSITORY% ^
  -Dfile=%JAR_LOCATION% ^
  -DgroupId=net.java.examples ^
  -DartifactId=examples ^
  -Dversion=1.0.0 ^
  -Dpackaging=jar