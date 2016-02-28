import java.util.regex.Pattern;

public class Constants {
    public final static String ACTIVITY_SUPERCLASS = "android.app.Activity";
    public final static String ONCLICK_LISTNER = "OnClickListener";
    public final static String ONCREATE_METHOD = "onCreate";
    public final static String CONTEXT_CLASS = "android.content.Context";
    public final static String JAVA_CLASS_CLASS = "java.lang.Class";



    public final static String INF_TRANS_OUTPUT_FILE = "inference_transformer_out.log";
    public final static String INF_TRANS_ERROR_FILE = "inference_transformer_err.log";
    public final static String INF_VISITOR_OUTPUT_FILE = "inference_visitor_out.log";
    public final static String INF_VISITOR_ERROR_FILE = "inference_visitor_err.log";

    public final static String ANON_VIEW_ONCLICK_LISTNER = "android.view.View$OnClickListener";


    public final static Pattern IS_ANON = Pattern.compile("^.*\\$\\d+$");
    public final static Pattern ANDROID_SKIP = Pattern.compile("^android\\..*$");

    public final static boolean PRINT_ST = true;
    public final static boolean DEBUG = true;
}

