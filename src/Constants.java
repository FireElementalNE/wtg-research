import java.util.regex.Pattern;

public class Constants {
    public final static String ACTIVITY_SUPERCLASS = "android.app.Activity";
    public final static String ONCLICK_LISTNER = "OnClickListener";
    public final static String ONCREATE_METHOD = "onCreate";
    public final static Pattern ANDROID_SKIP = Pattern.compile("^android\\..*$");
    public final static String CONTEXT_CLASS = "android.content.Context";
    public final static String JAVA_CLASS_CLASS = "java.lang.Class";

    public final static String OUTPUT_FILE = "./log.out";
    public final static String ERROR_FILE = "./log.err";

    public final static boolean PRINT_ST = false;
    public final static boolean DEBUG = true;
}

