import soot.*;
import soot.jimple.Jimple;
import soot.options.Options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainClass {

    public static void main(String[] args) {
        // TODO: see list
        // I need to find all cases of an intent, and link them back the their _Activity_ not just the declaring class
        // I also have to look for the actual call to intent.startActivityForResult

        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk);

        //output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_jimple);

        // resolve the PrintStream and System soot-classes
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

        // To remove messy shell scripting file!
        Options.v().set_force_android_jar(".");
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_whole_program(true);
        Options.v().set_verbose(Constants.CG_VERBOSE);

        // Exclude packages
        String[] excludes = new String[] {
                "android.annotation",
                "android.hardware",
                "android.support",
                "android.media",
                "com.android",
                "android.bluetooth",
                "android.media",
                "com.google",
                "com.yume.android",
                "com.squareup.okhttp",
                "com.crashlytics",
//            "com.nbpcorp.mobilead", // ad
//            "com.inmobi.androidsdk", //ad
//            "com.millennialmedia", //ad
//            "com.admob",  //ad
//            "com.admarvel.android.ads",  // ad
//            "com.mopub.mobileads",  //ad
//            "com.medialets", // ad
                "com.slidingmenu",
                "com.amazon.inapp.purchasing",
                "com.loopj",
                "com.appbrain",
                "com.heyzap.sdk",
                "net.daum.adam.publisher",
                "twitter4j.",
                "org.java_websocket",
                "org.acra",
                "org.apache"
        };
        List<String> exclude = new ArrayList<String>(Arrays.asList(excludes));
        Options.v().set_exclude(exclude);

        String apk_file = "";
        for(int i = 0; i < args.length; i++) {
            if(args[i].contains("-process-dir")) {
                apk_file = args[i+1];
            }
        }
        Options.v().set_whole_program(true);
        AndroidXMLUtility androidXMLUtility = new AndroidXMLUtility(apk_file);
        List<String> xml_files = androidXMLUtility.get_xml_names();
        for(String s : xml_files) {
            androidXMLUtility.parse_xml_file(s);
        }
        androidXMLUtility.write_elements();
        InferenceTransformer infTrans = new InferenceTransformer();

        /**
         * TODO
         * Maybe use wjtp for whole program analysis so we can get that pesky sootOutput/com.credgenfixed.R$id.jimple
         * this will allows us to map declared UI elements in the XML to ones found in the program (there is a
         * unique string)
         */
        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter1", infTrans));

        soot.Main.main(args);
        // TODO: this is how you can all classes. this will help u get com.credgenfixed.R$id
        // Chain<SootClass> sootClassChain = Scene.v().getClasses();
        infTrans.printAll();
        infTrans.print_ui_elements();
        WTGGraph graph = new WTGGraph(infTrans.get_nodes(), infTrans.get_edges(), Scene.v(), androidXMLUtility);
        graph.find_ui_element_class();

    }

    private static Local addTmpRef(Body body)
    {
        Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }

    private static Local addTmpString(Body body)
    {
        Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }
}
