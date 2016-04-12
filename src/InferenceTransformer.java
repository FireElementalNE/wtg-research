import soot.*;
import soot.util.Chain;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;


public class InferenceTransformer extends BodyTransformer {
    private Map <String, Map <Integer, String> > locals;
    private Map <String, List<String>> connections;
    private Map <String, String> connections2;
    private List<String> activities;
    private boolean has_run_cg;
    public LogWriter logWriter;
    public InferenceTransformer() {
        this.activities = new ArrayList<>();
        this.locals = new HashMap<>();
        this.connections = new HashMap<>();
        this.connections2 = new HashMap<>();
        try {
            /*this.logWriter = new LogWriter(Constants.INF_TRANS_OUTPUT_FILE,
                    Constants.INF_TRANS_ERROR_FILE);*/
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("InferenceTransformer: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    private boolean check_activity(SootClass sootClass) {
        if(!sootClass.hasSuperclass()) {
            return false;
        }
        else if(sootClass.hasSuperclass()) {
            SootClass method_superclass = sootClass.getSuperclass();
            if(method_superclass.getName().equals(Constants.ACTIVITY_SUPERCLASS)) {
                return true;
            }
            else {
                return check_activity(method_superclass);
            }
        }
        return false;
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        SootMethod method = body.getMethod();
        SootClass method_class = method.getDeclaringClass();
        // this finds all of the custom activities (activities that do not start with android.<something>)
        if(method_class.hasSuperclass() && method.isConstructor()) {
            // SootClass method_superclass = method_class.getSuperclass();
            if(check_activity(method_class)) {
                Matcher matcher = Constants.ANDROID_SKIP.matcher(method_class.getName());
                if(!matcher.find()) {
                    List <String> lst = new ArrayList<>();
                    lst.add(method.getName());
                    String msg = "Activity: " + method_class.getName();
                    System.out.println(msg);
                    this.logWriter.write_out(msg);
                    this.activities.add(method_class.getName());
                }
                else {
                    String msg = "Skipped: " + method_class.getName();
                    this.logWriter.write_out(msg);
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
        if(visitor.connections.keySet().size() > 0) {
            for(Map.Entry<String, List<String>> entry : visitor.connections.entrySet()) {
                if(!this.connections.keySet().contains(entry.getKey())) {
                    this.connections.put(entry.getKey(), entry.getValue());
                }
                else {
                    for(String string : visitor.connections.get(entry.getKey())) {
                        this.connections.get(entry.getKey()).add(string);
                    }
                }
            }
        }
    }
    public void printConnections() {
        for(Map.Entry<String, List<String>> entry : this.connections.entrySet()) {
            for(String string : entry.getValue()) {
                this.logWriter.write_out(entry.getKey() + " --> " + string);
            }
        }
    }
}
