import java.util.regex.Pattern;

public class Constants {
    public final static String ACTIVITY_SUPERCLASS = "android.app.Activity";
    public final static String SET_ONCLICK_LISTNER = "setOnClickListener";
    public final static String ONCLICK = "onClick";
    public final static String CONTEXT_CLASS = "android.content.Context";
    public final static String JAVA_CLASS_CLASS = "java.lang.Class";

    public final static String LOG_OUT_SUFFIX = "_out.log";
    public final static String LOG_ERR_SUFFIX = "_err.log";
    public final static String LOG_SCRATCH_SUFFIX = "_scratch.log";


    public final static String ANON_VIEW_ONCLICK_LISTNER = "android.view.View$OnClickListener";
    public final static String INTENT_CLASS = "android.content.Intent";

    public final static Pattern TARGET_ACTIVITY = Pattern.compile("\\(android\\.content\\.Context,java\\.lang.Class\\)\\>\\(\\$r\\d+, class \\\"([\\w\\W]+)\\\"\\)");
    public final static Pattern TARGET_INVOKE_LINE = Pattern.compile("<android\\.content\\.Intent: void <init>\\(android\\.content\\.Context,java\\.lang\\.Class\\)>");
    public final static Pattern ANDROID_SKIP = Pattern.compile("^android\\..*$");
    public final static Pattern ON_CLICK_LISTENER_CLASS = Pattern.compile("^android\\.view\\.View[\\$\\.]OnClickListener$");

    public final static boolean PRINT_ST = true;
    public final static boolean DEBUG = false;
    // Warning this is a lot of output
    public final static boolean CG_VERBOSE = false;
}

