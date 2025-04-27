@echo off

:: 设置本地仓库路径
set LOCAL_REPOSITORY=E:\apache-maven-3.9.4-bin\maven-repository

:: 设置JAR文件路径
set JAR_LOCATION=jna.jar

echo Initial LOCAL_REPOSITORY: %LOCAL_REPOSITORY%
echo Initial JAR_LOCATION: %JAR_LOCATION%

:: 检查文件是否存在
if not exist %JAR_LOCATION% (
  echo File %JAR_LOCATION% does not exist.
  goto :eof
)

:: 安装JNA依赖
echo Executing: mvn install:install-file -Dmaven.repo.local=%LOCAL_REPOSITORY% -Dfile=%JAR_LOCATION% -DgroupId=net.java.jna -DartifactId=jna -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file ^
  -Dmaven.repo.local=%LOCAL_REPOSITORY% ^
  -Dfile=%JAR_LOCATION% ^
  -DgroupId=net.java.hk.jna ^
  -DartifactId=hkjna ^
  -Dversion=1.0.0 ^
  -Dpackaging=jar