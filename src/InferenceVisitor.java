import soot.*;
import soot.jimple.*;
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
    public void storePossibleCallers(SootMethod target, String callee) {
        CallGraph cg = Scene.v().getCallGraph();
        Iterator sources = new Sources(cg.edgesInto(target));
        while (sources.hasNext()) {
            SootMethod src = (SootMethod)sources.next();
            SootClass sootClass = src.getDeclaringClass();
            if(!this.connections.keySet().contains(sootClass.getName())) {
                this.connections.put(sootClass.getName(), new ArrayList<String>());
            }
            this.connections.get(sootClass.getName()).add(callee);
            // this.logWriter.write_out(target + " might be called by " + src);
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
                    && method_class.getName().contains("Intent")) {
                ValueBox valueBox = stmt.getInvokeExprBox();
                // this.logWriter.write_out("VALUE BOCX: " + valueBox.getValue().toString());
                Matcher matcher = Constants.TARGET_ACTIVITY.matcher(valueBox.getValue().toString());
                if(matcher.find()) {
                    this.logWriter.write_out("Found Match!");
                    storePossibleCallers(method, matcher.group(1));
                }
            }
        }
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        /*Value left_op = stmt.getLeftOp();
        Value right_op = stmt.getRightOp();
        if(stmt.containsInvokeExpr()) {
            SootMethod method = stmt.getInvokeExpr().getMethod();
            SootClass method_class = method.getDeclaringClass();
            this.logWriter.write_out(method.toString());
        }*/
    }



}
