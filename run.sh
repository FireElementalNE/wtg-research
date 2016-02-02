rm -rf sootOutput
android_jars="."
process_dir="test_program/Loop1/Loop1.apk"
mylib=`find ./classpath_includes/ -name '*.jar' | xargs | sed 's/ /:/g'`
mylib2="out/production/wtg-research/:soot-trunk.jar"
java -classpath $mylib2 MainClass --soot-class-path $mylib -process-dir $process_dir -android-jars $android_jars 
