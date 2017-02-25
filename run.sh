#!/bin/bash

# defaults
DEFAULT_JAR="test_program/credgen/CredGen_Final.apk"
JAVA_LIBS=`find ./classpath_includes/ -name '*.jar' | xargs | sed 's/ /:/g'`
SOOT_TRUNK="src:classpath_includes/soot-trunk.jar"
MAIN_CLASS="src/MainClass.java"
DEFAULT_MEM="8196M"

clean() {
  rm -rf sootOutput 
  rm -rf src/*.class src/androidgraph/*.class *.log
  rm -rf graph_generator/*.dot graph_generator/*.svg
  rm -rf graph_generator/*.pyc graph_generator/globals/*.pyc
}

usage() { 
  printf "Usage: $0 [-v] [-t <ANDROID APK>] [-m <MEMORY>]\n" 1>&2 
  printf "\t-v shows soot's (very) verbose output\n" 1>&2
  printf "\t-t <ANDROID APK> sets the target apk\n" 1>&2
  printf "\t-m <MEMORY> sets the max java memory (-Xmx)\n" 1>&2
  printf "\t-c clean up dirs\n" 1>&2
  exit 1
}
while getopts ":t:vcm:" opt; do
  case $opt in
    v)
      VERBOSE=1
      ;;
    t)
	  TARGET_JAR="$OPTARG"
	  ;;
    m)
      MAX_MEM="$OPTARG"
      ;;
    c)
      clean
      exit
      ;;
    \?)
      usage
      ;;
  esac
done

clean

# set memory and pretty print
if [ $MAX_MEM ] ; then
    DEFAULT_MEM="$MAX_MEM"
fi
printf "Max Memory.....%s\n" $DEFAULT_MEM

# set target and pretty print
if [ $TARGET_JAR ] ; then
    DEFAULT_JAR="$TARGET_JAR"
fi
printf "Targeting......%s\n" $DEFAULT_JAR

# pretty print compilation and compile
javac $MAIN_CLASS -classpath "./classpath_includes/soot-trunk.jar:src/"
printf "Compilation...."
if [ $? != 0 ] ; then
    printf "failed\n"
    exit $?
else
    printf "successful\n"
fi

# The lolz: https://mailman.cs.mcgill.ca/pipermail/soot-list/2015-June/008074.html
printf "Verbose........"
if [ $VERBOSE ] ; then
    printf "enabled\n"
	java "-Xmx$DEFAULT_MEM" -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_JAR
else
    printf "disabled\n"
	start=`date +%s`
	printf "Running Soot..."
	java "-Xmx$DEFAULT_MEM" -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_JAR &>/dev/null
    printf "Done\n"
    end=`date +%s`
    RUNTIME=$((end-start))
    printf "Runtime........%s seconds\n" $RUNTIME
fi

# printf "Generating Graph..."
# python graph_generator/gen_graph.py -i InferenceTransformer_logfile.log
# printf "Done."