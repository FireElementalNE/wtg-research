import soot.*;
import soot.jimple.*;
import soot.jimple.internal.InvokeExprBox;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Sources;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InferenceVisitor extends AbstractStmtSwitch {
    public LogWriter logWriter;
    private InferenceTransformer t;
    public Map<String, List<String>> connections;
    public InferenceVisitor(InferenceTransformer inferenceTransformer) {
        this.t = inferenceTransformer;
        this.connections = new HashMap<>();
        try {
            /*this.logWriter = new LogWriter(Constants.INF_VISITOR_OUTPUT_FILE,
                    Constants.INF_VISITOR_ERROR_FILE);*/
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("InferenceVisitor: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }
    private boolean checkEdge(String activityBody, String callee) {
        String lines[] = activityBody.split("\\r?\\n");
        for(int i = 0; i < lines.length; i++) {
            String testString = lines[i].replace("\n", "").replace("\r", "");
            Matcher invokeMatcher = Constants.TARGET_INVOKE_LINE.matcher(testString);
            if(invokeMatcher.find()) {
                Matcher classMatcher = Constants.TARGET_ACTIVITY.matcher(testString);
                if(classMatcher.find()) {
                    if(classMatcher.group(1).equals(callee)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void storePossibleCallersIntent(SootMethod target, String callee) {
        CallGraph cg = Scene.v().getCallGraph();
        Iterator sources = new Sources(cg.edgesInto(target));
        while (sources.hasNext()) {
            SootMethod src = (SootMethod)sources.next();
            SootClass sootClass = src.getDeclaringClass();
            // TODO: make this more concrete, pretty hackish
            // TODO: fix made it a _bit_ less hackish but still pretty hackish
            // TODO: issue remains
            if(checkEdge(src.getActiveBody().toString(), callee)) {
                if (!this.connections.keySet().contains(sootClass.getName())) {
                    this.logWriter.write_out(src.getName());
                    this.connections.put(sootClass.getName(), new ArrayList<String>());
                }
                if(!this.connections.get(sootClass.getName()).contains(callee)) {
                    this.connections.get(sootClass.getName()).add(callee);
                }
            }
        }
    }
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        if(stmt.containsInvokeExpr()) {
            SootMethod method = stmt.getInvokeExpr().getMethod();
            SootClass method_class = method.getDeclaringClass();
            if(method.isConstructor()
                    && method.getParameterCount() == 2
                    && method.getParameterType(0).toString().equals(Constants.CONTEXT_CLASS)
                    && method.getParameterType(1).toString().contains(Constants.JAVA_CLASS_CLASS)
                    && method_class.getName().equals(Constants.INTENT_CLASS)) {
                ValueBox valueBox = stmt.getInvokeExprBox();
                Matcher matcher = Constants.TARGET_ACTIVITY.matcher(valueBox.getValue().toString());
                if(matcher.find()) {
                    storePossibleCallersIntent(method, matcher.group(1));
                }
            }
            else if(method.getName().contains("setOnClickListener")) {
                // TODO: work on getting correct widget type
                // Idea: to do this, look for all constructor calls to button ect, then store hash in hashmap
                // then make annother hashmap for all the calls to setOnclicklistner (the objects that call it)
                // after complete traversal is done connect the two. Will also have to some how connect that
                // to each edge...
            }
        }
    }
}
