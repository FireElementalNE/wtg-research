import java.util.*;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;


public class MainClass {

    public static void main(String[] args) {

        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk);

        //output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_jimple);

        // resolve the PrintStream and System soot-classes
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

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

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {
            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                System.out.println("LINDSEY: ".concat(phaseName));
                List<Local> local_list = b.getParameterLocals();
                for(int i = 0; i < local_list.size(); i++) {
                    String outStr = String.format("\tLOCALd #%d: %s", i, local_list.get(i).getName());
                    System.out.println(outStr);
                }
            }

        }));
        soot.Main.main(args);
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