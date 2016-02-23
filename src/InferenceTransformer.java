import soot.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InferenceTransformer extends BodyTransformer {
    private List<String> activities;
    public LogWriter logWriter;
    public InferenceTransformer() {
        this.activities = new ArrayList<>();
        try {
            this.logWriter = new LogWriter();
        } catch (IOException e) {
            System.err.println("InferenceTransformer: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
            logWriter.write_stackdump(e);
        }
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        // TODO: Fix This, I cannot get the correct method.
        // specialinvoke $r2.<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>($r3, class "com/credgenfixed/PasswordGen");
        // specialinvoke $r2.<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>($r3, class "com/credgenfixed/UsernameGen");
        SootMethod method = body.getMethod();
        SootClass method_class = method.getDeclaringClass();
        Type ret_type = method.getReturnType();
        List <Type> types = method.getParameterTypes();
        if(method.isConstructor()
                && method.getParameterCount() > 0
                && ret_type.toString().equals("void")) {
            if(method.getParameterType(0).toString().equals(Constants.CONTEXT_CLASS)) {
                for(Type type : types) {
                    // never passes next if statement
                    if(type.toString().contains(Constants.JAVA_CLASS_CLASS)) {
                        logWriter.write_out(method.getName());
                        logWriter.write_out("\n\n");
                        break;
                    }
                }
            }
        }
        // this finds all of the custom activities (activities that do not start with android.<something>
        if(method.getName().equals(Constants.ONCREATE_METHOD) && method_class.hasSuperclass()) {
            SootClass method_superclass = method_class.getSuperclass();
            if(method_superclass.getName().equals(Constants.ACTIVITY_SUPERCLASS)) {
                Matcher matcher = Constants.ANDROID_SKIP.matcher(method_class.getName());
                if(!matcher.find()) {
                    String msg = "Activity: " + method.getDeclaringClass().getName();
                    this.logWriter.write_out(msg);
                    this.activities.add(method.getDeclaringClass().getName());
                }
            }
        }
    }
}
