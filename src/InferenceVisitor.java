import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.ValueBox;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Sources;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;


public class InferenceVisitor extends AbstractStmtSwitch {
    public LogWriter logWriter;
    public Map<String, List<String>> edges;
    public List<String> UIElements;

    /**
     * Constructor
     */
    public InferenceVisitor() {
        this.UIElements = new ArrayList<>();
        this.edges = new HashMap<>();
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("InferenceVisitor: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks that the current active body has an exact invoke line to the target Activity
     * @param activityBody the current active body
     * @param callee the class that is being called through an intent
     * @return true iff the callee is being invoked
     */
    private boolean checkIntentCallEdge(String activityBody, String callee) {
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

    /**
     * Stores possible Intent callers, prelim to creating an edge
     * @param target the method that has the intent call
     * @param callee the target Activity of the intent call
     */
    private void storePossibleCallersIntent(SootMethod target, String callee) {
        CallGraph cg = Scene.v().getCallGraph();
        Iterator sources = new Sources(cg.edgesInto(target));
        while (sources.hasNext()) {
            SootMethod src = (SootMethod)sources.next();
            SootClass srcClass = src.getDeclaringClass();
            // TODO: make this more concrete, pretty hackish
            // TODO: fix made it a _bit_ less hackish but still pretty hackish
            // TODO: issue remains
            if(checkIntentCallEdge(src.getActiveBody().toString(), callee)) {
                if (!this.edges.keySet().contains(srcClass.getName())) {
                    this.edges.put(srcClass.getName(), new ArrayList<String>());
                }
                if(!this.edges.get(srcClass.getName()).contains(callee)) {
                    this.edges.get(srcClass.getName()).add(callee);
                }
            }
        }
    }

    /**
     * Overidden statment to catch invoke statements
     * @param stmt the current invoke statement
     */
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        if(stmt.containsInvokeExpr()) {
            SootMethod method = stmt.getInvokeExpr().getMethod();
            SootClass methodClass = method.getDeclaringClass();
            if(method.isConstructor()
                    && method.getParameterCount() == 2
                    && method.getParameterType(0).toString().equals(Constants.CONTEXT_CLASS)
                    && method.getParameterType(1).toString().contains(Constants.JAVA_CLASS_CLASS)
                    && methodClass.getName().equals(Constants.INTENT_CLASS)) {
                ValueBox valueBox = stmt.getInvokeExprBox();
                Matcher matcher = Constants.TARGET_ACTIVITY.matcher(valueBox.getValue().toString());
                if(matcher.find()) {
                    storePossibleCallersIntent(method, matcher.group(1));
                }
            }
        }
    }
}
