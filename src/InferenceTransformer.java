import soot.Body;
import soot.BodyTransformer;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by fire on 2/2/16.
 */
public class InferenceTransformer extends BodyTransformer {
    private final String ACTIVITY_SUPERCLASS = "android.app.Activity";
    private final String ONCREATE_METHOD = "onCreate";
    private Pattern ANDROID_SKIP = Pattern.compile("^android\\..*$");
    private List<String> activities;
    public InferenceTransformer() {
        this.activities = new ArrayList<>();
    }
    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        SootMethod method = body.getMethod();
        SootClass method_class = method.getDeclaringClass();
        if(method.getName().equals(this.ONCREATE_METHOD) && method_class.hasSuperclass()) {
            SootClass method_superclass = method_class.getSuperclass();
            if(method_superclass.getName().equals(this.ACTIVITY_SUPERCLASS)) {
                Matcher matcher = this.ANDROID_SKIP.matcher(method_class.getName());
                if(!matcher.find()) {
                    System.out.println("Activity: " + method.getDeclaringClass().getName());
                    this.activities.add(method.getDeclaringClass().getName());
                }
            }
        }
    }
}
