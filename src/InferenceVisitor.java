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
            this.logWriter = new LogWriter(Constants.INF_VISITOR_OUTPUT_FILE,
                    Constants.INF_VISITOR_ERROR_FILE);
        } catch (IOException e) {
            System.err.println("InferenceVisitor: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }
    public void storePossibleCallersIntent(SootMethod target, String callee) {
        CallGraph cg = Scene.v().getCallGraph();
        Iterator sources = new Sources(cg.edgesInto(target));
        while (sources.hasNext()) {
            SootMethod src = (SootMethod)sources.next();
            SootClass sootClass = src.getDeclaringClass();
            // TODO: make this more concrete, pretty hackish
            if(src.getActiveBody().toString().contains(callee)) {
                if (!this.connections.keySet().contains(sootClass.getName())) {
                    this.logWriter.write_out(src.getName());
                    this.connections.put(sootClass.getName(), new ArrayList<String>());
                }
                this.connections.get(sootClass.getName()).add(callee);
                int i = 0;
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
                String widget_type = stmt.getInvokeExprBox().getValue().getType().toString();
            }
        }
    }
}
