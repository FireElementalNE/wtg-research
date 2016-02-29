rm -rf sootOutput *.log
rm -rf src/*.class
TARGET_JAR="test_program/credgen/CredGen_Final.apk"
JAVA_LIBS=`find ./classpath_includes/ -name '*.jar' | xargs | sed 's/ /:/g'`
SOOT_TRUNK="src:classpath_includes/soot-trunk.jar"
MAIN_CLASS="src/MainClass.java"
echo "Compiling $MAIN_CLASS"
javac $MAIN_CLASS -classpath "./classpath_includes/soot-trunk.jar:src/" # --soot-class-path $mylib -process-dir $process_dir -android-jars $android_jars
echo "Done."
# TODO: consider using &>/dev/null...
if [[ $# -eq 0 ]] ; then
    echo "Targeting: $TARGET_JAR"
    java -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $TARGET_JAR
    exit 0
else
	echo "Targeting: $1"
	java -classpath $SOOT_TRUNK MainClass --soot-class-path $JAVA_LIBS -process-dir $1
fi

