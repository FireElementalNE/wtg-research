import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Constants {
    final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    final static String ACTIVITY_SUPERCLASS = "android.app.Activity";
    final static String SET_ONCLICK_LISTNER = "setOnClickListener";
    final static String ONCLICK = "onClick";
    final static String CONTEXT_CLASS = "android.content.Context";
    final static String ON_CLICK_LISTENER_CLASS = "android.view.ViewOnClickListener";
    final static String JAVA_CLASS_CLASS = "java.lang.Class";

    final static String LOG_OUT_SUFFIX = "_out.log";
    final static String LOG_ERR_SUFFIX = "_err.log";
    final static String LOG_SCRATCH_SUFFIX = "_scratch.log";


    final static String INTENT_CLASS = "android.content.Intent";

    final static Pattern TARGET_ACTIVITY = Pattern.compile("\\(android\\.content\\.Context,java\\.lang.Class\\)\\>\\(\\$r\\d+, class \\\"([\\w\\W]+)\\\"\\)");
    final static Pattern TARGET_INVOKE_LINE = Pattern.compile("<android\\.content\\.Intent: void <init>\\(android\\.content\\.Context,java\\.lang\\.Class\\)>");
    final static Pattern ANDROID_SKIP = Pattern.compile("^android\\..*$");


    final static boolean PRINT_ST = true;
    final static boolean DEBUG = false;
    // Warning this is a lot of output
    final static boolean CG_VERBOSE = false;
}

