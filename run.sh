rm -rf sootOutput *.log
rm -rf src/*.class
android_jars="."
process_dir="test_program/credgen/CredGen_Final.apk"
mylib=`find ./classpath_includes/ -name '*.jar' | xargs | sed 's/ /:/g'`
mylib2="src:classpath_includes/soot-trunk.jar"
main_class="src/MainClass.java"
javac $main_class -classpath "./classpath_includes/soot-trunk.jar:src/" # --soot-class-path $mylib -process-dir $process_dir -android-jars $android_jars 
java -classpath $mylib2 MainClass --soot-class-path $mylib -process-dir $process_dir -force-android-jar $android_jars -ire -src-prec apk -allow-phantom-refs 
