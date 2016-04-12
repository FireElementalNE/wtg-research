import soot.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;


public class InferenceTransformer extends BodyTransformer {
    private Map <String, List<String>> edges;
    private List<String> nodes;
    private List<String> UIElements;
    public LogWriter logWriter;

    public InferenceTransformer() {
        this.nodes = new ArrayList<>();
        this.edges = new HashMap<>();
        this.UIElements = new ArrayList<>();
        try {
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
        SootClass methodClass = method.getDeclaringClass();
        // this finds all of the custom activities (activities that do not start with android.<something>)
        if(methodClass.hasSuperclass() && method.isConstructor()) {
            // SootClass method_superclass = method_class.getSuperclass();
            if (check_activity(methodClass)) {
                Matcher matcher = Constants.ANDROID_SKIP.matcher(methodClass.getName());
                if (!matcher.find()) {
                    this.nodes.add(methodClass.getName());
                } else {
                    String msg = "Skipped: " + methodClass.getName();
                    this.logWriter.write_out(msg);
                }
            }
        }
        final PatchingChain<Unit> units = body.getUnits();
        InferenceVisitor visitor = new InferenceVisitor();
        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
            final Unit u = iter.next();
            u.apply(visitor);
        }
        if(visitor.edges.keySet().size() > 0) {
            for(Map.Entry<String, List<String>> entry : visitor.edges.entrySet()) {
                if(!this.edges.keySet().contains(entry.getKey())) {
                    this.edges.put(entry.getKey(), entry.getValue());
                }
                else {
                    for(String string : visitor.edges.get(entry.getKey())) {
                        this.edges.get(entry.getKey()).add(string);
                    }
                }
            }
        }
        for(String el : visitor.UIElements) {
            this.UIElements.add(el);
        }
    }

    public void printAll() {
        printNodes();
        printEdges();
        printUIElements();
    }

    private void printNodes() {
        for(String entry : this.nodes) {
            this.logWriter.write_out("Activity: " + entry);
        }
    }
    private void printEdges() {
        for(Map.Entry<String, List<String>> entry : this.edges.entrySet()) {
            for(String string : entry.getValue()) {
                this.logWriter.write_out(entry.getKey() + " --> " + string);
            }
        }
    }
    private void printUIElements() {
        for(String el : this.UIElements) {
            this.logWriter.write_scratch(el);
        }
    }
}
