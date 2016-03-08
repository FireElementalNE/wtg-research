rm -rf sootOutput *.log
rm -rf src/*.class
DEFAULT_JAR="test_program/credgen/CredGen_Final.apk"
JAVA_LIBS=`find ./classpath_includes/ -name '*.jar' | xargs | sed 's/ /:/g'`
SOOT_TRUNK="src:classpath_includes/soot-trunk.jar"
MAIN_CLASS="src/MainClass.java"
echo "Compiling $MAIN_CLASS"
javac $MAIN_CLASS -classpath "./classpath_includes/soot-trunk.jar:src/" # --soot-class-path $mylib -process-dir $process_dir -android-jars $android_jars
echo "Done."
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
# The lolz: https://mailman.cs.mcgill.ca/pipermail/soot-list/2015-June/008074.html
if [ $VERBOSE ] ; then
	echo "VERBOSE: ON"
	if [ $TARGET_JAR ] ; then
    	echo "Targeting: $TARGET_JAR"
    	java -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $TARGET_JAR
    else
    	echo "Targeting: $DEFAULT_JAR"
    	java -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_JAR
    fi
else
	echo "VERBOSE: OFF"
	echo "Running Soot."
	start=`date +%s`
	if [ $TARGET_JAR ] ; then
    	echo "Targeting: $TARGET_JAR"
    	java -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $TARGET_JAR &>/dev/null
    else
    	echo "Targeting: $DEFAULT_JAR"
    	java -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $DEFAULT_JAR &>/dev/null
    fi
    echo "Done."
    end=`date +%s`
    runtime=$((end-start))
    echo "Runtime: $runtime seconds"
fi

