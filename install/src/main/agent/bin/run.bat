@echo off
set JAVA_HOME=c:\java\jdk8
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
set SPRINGBOOT=%USERPROFILE%\bin\springboot.jar

set SYSMON_VERSION=develop-SNAPSHOT
set SYSMON_MODULE=agent
set SYSMON_ARTIFACT=io.jrevolt.sysmon:io.jrevolt.sysmon.%SYSMON_MODULE%:%SYSMON_VERSION%

set MVNLAUNCHER_OPTIONS=--MvnLauncher.repositoryUrl=https://build.dcom.sk/nexus/content/groups/public/ --MvnLauncher.updateInterval=0
set SPRINGBOOT_OPTIONS=--logging.file=%USERPROFILE%\var\log\sysmon\%SYSMON_MODULE%.log
set SYSMON_OPTIONS=--sysmon.agent.clusterName=soa --sysmon.agent.serverName=si1soa01.dev.dcom.sk

%JAVA_EXE% -jar %SPRINGBOOT% %SYSMON_ARTIFACT% %MVNLAUNCHER_OPTIONS% %SPRINGBOOT_OPTIONS% %SYSMON_OPTIONS%