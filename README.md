# Android Window Transition Graphs

This program is attempting to create Android Window transition graphs for  
the static analyzer [**DroidInfer**](https://github.com/proganalysis/type-inference).

# Running
[**run.sh**](https://github.com/FireElementalNE/wtg-research/blob/master/run.sh) runs the framework. It assumes that all required jars are  
in a folder named 'classpath_includes' in the root (the same directory  
  as this file).  
  An example classpath_includes folder is shown below:  
```
|-- android-4.3.jar
|-- android-all.jar
|-- cfl.jar
|-- cgs.jar
|-- jdk-android.jar
|-- jre
|   `-- lib
|       |-- alt-rt.jar
|       |-- charsets.jar
|       |-- deploy.jar
|       |-- ext
|       |   |-- dnsns.jar
|       |   |-- localedata.jar
|       |   |-- sunec.jar
|       |   |-- sunjce_provider.jar
|       |   |-- sunpkcs11.jar
|       |   `-- zipfs.jar
|       |-- javaws.jar
|       |-- jce.jar
|       |-- jfr.jar
|       |-- jfxrt.jar
|       |-- jsse.jar
|       |-- management-agent.jar
|       |-- plugin.jar
|       |-- resources.jar
|       |-- rt.jar
|       `-- security
|           |-- local_policy.jar
|           `-- US_export_policy.jar
|-- libs
|   |-- core.jar
|   |-- framework2.jar
|   |-- framework3.jar
|   `-- framework.jar
|-- soot-develop.jar
`-- soot-trunk.jar

5 directories, 31 files
```

This was created by running:  
```shell
tree -S -P "*.jar" --charset=ascii --prune -n
```
run.sh has the following usage:  
```
bash $ ./run.sh -h
Usage: ./run.sh [-v] [-t <ANDROID APK>] [-m <MEMORY>]
	-v shows soot's (very) verbose output
	-t <ANDROID APK> sets the target apk
	-m <MEMORY> sets the max java memory (-Xmx)
```

# Requirements
* Java JDK (you need javac)
* All of the classes that will be analyzed (anything not explicitly given  
  will be treated as a phantom-ref!)  
* [**Graphviz**](http://www.graphviz.org/) (this is available for most Linux distributions)

# Notes
* The original WTG paper can be found [**here**](http://dacongy.github.io/papers/yang-ase15.pdf).
* JavaDoc for soot can be found in [**docs/src**](https://github.com/FireElementalNE/wtg-research/tree/master/docs/soot)
