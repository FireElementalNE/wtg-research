#!/bin/bash

# defaults
DEFAULT_APK="test_program/credgen/CredGen_Final.apk"
JAVA_LIBS=`find ./classpath_includes/ -name '*.jar' | xargs | sed 's/ /:/g'`
SOOT_TRUNK="src:classpath_includes/soot-trunk.jar"
MAIN_CLASS="src/MainClass.java"
DEFAULT_MEM="8196M"
APK_TOOL="./apktool.jar"

clean() {
  rm -rf sootOutput 
  rm -rf src/*.class src/androidgraph/*.class *.log
  rm -rf graph_generator/*.dot graph_generator/*.svg
  rm -rf graph_generator/*.pyc graph_generator/globals/*.pyc
  rm -rf APK_UNPACK
}

usage() { 
  printf "Usage: $0 [-v] [-t <ANDROID APK>] [-m <MEMORY>] [-c] [-X] [-h]\n" 1>&2 
  printf "\t-h print this help message and exit\n"
  printf "\t-v shows soot's (very) verbose output\n" 1>&2
  printf "\t-t <ANDROID APK> sets the target apk\n" 1>&2
  printf "\t-m <MEMORY> sets the max java memory (-Xmx)\n" 1>&2
  printf "\t-X skip running apktool\n" 1>&2
  printf "\t-c clean up dirs\n" 1>&2
  exit 1
}
while getopts ":t:vcm:Xh" opt; do
  case $opt in
    v)
      VERBOSE=1
      ;;
    t)
	  TARGET_APK="$OPTARG"
	  ;;
    m)
      MAX_MEM="$OPTARG"
      ;;
    c)
      clean
      exit
      ;;
    X)
      SKIPAPKTOOL=1
      ;;
    h)
      usage
      exit
      ;;
    \?)
      usage
      ;;
  esac
done

clean

if ! [ $SKIPAPKTOOL ] ; then
  if [ $VERBOSE ] ; then
      java "-Xmx$DEFAULT_MEM" -Dfile.encoding=utf-8 -jar $APK_TOOL -o APK_UNPACK d $DEFAULT_APK
  else
      printf "Running apktool..."
      start=`date +%s`
      java "-Xmx$DEFAULT_MEM" -Dfile.encoding=utf-8 -jar $APK_TOOL -o APK_UNPACK d $DEFAULT_APK &>/dev/null
      end=`date +%s`
      APK_TOOL_RUNTIME=$((end-start))
      printf "Done.\n"
      printf "apktool runtime...%s seconds\n" $APK_TOOL_RUNTIME
  fi
else
  printf "Apktool...........skipped\n"
fi
# set memory and pretty print
if [ $MAX_MEM ] ; then
    DEFAULT_MEM="$MAX_MEM"
fi
printf "Max Memory........%s\n" $DEFAULT_MEM

# set target and pretty print
if [ $TARGET_APK ] ; then
    DEFAULT_APK="$TARGET_JAR"
fi
printf "Targeting.........%s\n" $DEFAULT_APK

# pretty print compilation and compile
javac $MAIN_CLASS -classpath "./classpath_includes/soot-trunk.jar:src/"
printf "Compilation......."
if [ $? != 0 ] ; then
    printf "failed\n"
    exit $?
else
    printf "successful\n"
fi

# The lolz: https://mailman.cs.mcgill.ca/pipermail/soot-list/2015-June/008074.html
printf "Verbose..........."
if [ $VERBOSE ] ; then
    printf "enabled\n"
	java "-Xmx$DEFAULT_MEM" -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_APK
else
    printf "disabled\n"
	start=`date +%s`
	printf "Running Soot......"
	java "-Xmx$DEFAULT_MEM" -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_APK &>/dev/null
    printf "Done\n"
    end=`date +%s`
    SOOT_RUNTIME=$((end-start))
    printf "Soot runtime......%s seconds\n" $SOOT_RUNTIME
fi
