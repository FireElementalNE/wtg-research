import soot.*;
import soot.util.Chain;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InferenceTransformer extends BodyTransformer {
    private Map <String, Map <Integer, String> > locals;
    private List<String> activities;
    public LogWriter logWriter;
    public InferenceTransformer() {
        this.activities = new ArrayList<>();
        this.locals = new HashMap<>();
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
        SootMethod method = body.getMethod();
        SootClass method_class = method.getDeclaringClass();
        // this finds all of the custom activities (activities that do not start with android.<something>)
        if(method_class.hasSuperclass() && method.isConstructor()) {
            SootClass method_superclass = method_class.getSuperclass();
            if(method_superclass.getName().equals(Constants.ACTIVITY_SUPERCLASS)) {
                Matcher matcher = Constants.ANDROID_SKIP.matcher(method_class.getName());
                if(!matcher.find()) {
                    List <String> lst = new ArrayList<>();
                    lst.add(method.getName());
                    String msg = "Activity: " + method_class.getName();
                    System.out.println(msg);
                    this.logWriter.write_out(msg);
                    this.activities.add(method_class.getName());
                }
            }
        }
        if(this.activities.contains(method_class.getName())
                && method.getName().equals(Constants.ONCREATE_METHOD)) {
            Chain <Local> class_local_list = body.getLocals();
            Map <Integer, String> class_locals = new HashMap<>();
            for(Local local : class_local_list) {
                int key = local.hashCode();
                String value = local.getName() + " <--> " + local.getType();
                class_locals.put(key, value);
            }
            this.locals.put(method_class.getName(), class_locals);
        }
        final PatchingChain<Unit> units = body.getUnits();
        InferenceVisitor visitor = new InferenceVisitor(this);
        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
            final Unit u = iter.next();
            u.apply(visitor);
        }
    }
}
