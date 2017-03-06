import android.content.res.AXmlResourceParser;
import org.xmlpull.v1.XmlPullParser;
import soot.*;
import soot.jimple.Jimple;
import soot.options.Options;
import test.AXMLPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class MainClass {
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

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
        // TODO: expand this, thanks internet and Scene.java!

        // get AndroidManifest
        File apkF = new File(apk_file);
        InputStream manifestIS = null;
        ZipFile archive = null;
        try {
            archive = new ZipFile(apkF);
            for (@SuppressWarnings("rawtypes") Enumeration entries = archive.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                // We are dealing with the Android manifest
                if (entryName.equals("AndroidManifest.xml")) {
                    manifestIS = archive.getInputStream(entry);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error when looking for manifest in apk: " + e);
        }
        try {
            AXmlResourceParser parser = new AXmlResourceParser();
            parser.open(manifestIS);
            while (true) {
                int type = parser.next();
                if (type == XmlPullParser.END_DOCUMENT) {
                    // throw new RuntimeException
                    // ("target sdk version not found in Android manifest ("+
                    // apkF +")");
                    break;
                }
                switch (type) {
                    case XmlPullParser.START_DOCUMENT: {

                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        String tagName = parser.getName();
                        System.out.println(tagName);
                        break;
                    }
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        InferenceTransformer infTrans = new InferenceTransformer();
        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter1", infTrans));
        // TODO: Need to make a couple of passes here to find out the type of call graph
        // TODO: is being generated.
        soot.Main.main(args);
        infTrans.printAll();
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
