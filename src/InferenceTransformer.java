import soot.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
            this.logWriter = new LogWriter(Constants.INF_TRANS_OUTPUT_FILE,
                    Constants.INF_TRANS_ERROR_FILE);
        } catch (IOException e) {
            System.err.println("InferenceTransformer: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        // TODO: Fix This, I cannot get the correct method.
        // specialinvoke $r2.<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>($r3, class "com/credgenfixed/PasswordGen");
        // specialinvoke $r2.<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>($r3, class "com/credgenfixed/UsernameGen");
        SootMethod method = body.getMethod();
        SootClass method_class = method.getDeclaringClass();

        // this finds all of the custom activities (activities that do not start with android.<something>
        if(method.getName().equals(Constants.ONCREATE_METHOD) && method_class.hasSuperclass()) {
            SootClass method_superclass = method_class.getSuperclass();
            if(method_superclass.getName().equals(Constants.ACTIVITY_SUPERCLASS)) {
                Matcher matcher = Constants.ANDROID_SKIP.matcher(method_class.getName());
                if(!matcher.find()) {
                    String msg = "Activity: " + method.getDeclaringClass().getName();
                    System.out.println(msg);
                    this.logWriter.write_out(msg);
                    this.activities.add(method.getDeclaringClass().getName());
                }
            }
        }
        final PatchingChain<Unit> units = body.getUnits();
        InferenceVisitor visitor = new InferenceVisitor(this);
        for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
            final Unit u = iter.next();
            u.apply(visitor);
        }
    }
}
