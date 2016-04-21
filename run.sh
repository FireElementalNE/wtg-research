#!/bin/bash
rm -rf sootOutput *.log
rm -rf src/*.class
DEFAULT_JAR="test_program/credgen/CredGen_Final.apk"
JAVA_LIBS=`find ./classpath_includes/ -name '*.jar' | xargs | sed 's/ /:/g'`
SOOT_TRUNK="src:classpath_includes/soot-trunk.jar"
MAIN_CLASS="src/MainClass.java"
JAVA_MEM="-Xmx8196m"
# TODO: consider using &>/dev/null...
usage() { echo "Usage: $0 [-v] [-t <android jar>]" 1>&2; exit 1; }
while getopts ":t:v" opt; do
  case $opt in
    v)
      VERBOSE=1
      ;;
    t)
	  TARGET_JAR="$OPTARG"
	  ;;
    \?)
	  usage
	  ;;
  esac
done
printf "Compiling %s..." $MAIN_CLASS
javac $MAIN_CLASS -classpath "./classpath_includes/soot-trunk.jar:src/" # --soot-class-path $mylib -process-dir $process_dir -android-jars $android_jars
printf "Done.\n"
# The lolz: https://mailman.cs.mcgill.ca/pipermail/soot-list/2015-June/008074.html
printf "Verbose..."
if [ $VERBOSE ] ; then
	printf "enabled.\n"
	if [ $TARGET_JAR ] ; then
    	printf "Targeting: %s\n" $TARGET_JAR
    	java $JAVA_MEM -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $TARGET_JAR
    else
    	printf "Targeting: %s\n"
    	java $JAVA_MEM -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_JAR
    fi
else
	printf "disabled.\n"
	start=`date +%s`
	if [ $TARGET_JAR ] ; then
    	printf "Targeting: %s\n" $TARGET_JAR
	printf "Running Soot..."
    	java $JAVA_MEM -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $TARGET_JAR &>/dev/null
    else
    	printf "Targeting: %s\n" $DEFAULT_JAR
	printf "Running Soot..."
    	java $JAVA_MEM -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_JAR &>/dev/null
    fi
    printf "Done.\n"
    end=`date +%s`
    runtime=$((end-start))
    echo "Runtime: $runtime seconds"
fi

