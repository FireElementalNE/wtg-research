import androidgraph.WTGGraphNode;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Sources;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;


class InferenceVisitor extends AbstractStmtSwitch {
    private LogWriter logWriter;
    Map<String, List<String>> edges;
    private SootClass activity_class;
    private List<WTGGraphNode> graph_nodes;
    private int pass_number;

    /**
     * Constructor for first pass
     * @param pass_number the pass number
     * @param activityclass the activity class
     * @param graph_nodes the current list of WTGGraphNodes
     */
    InferenceVisitor(SootClass activityclass, List<WTGGraphNode> graph_nodes, int pass_number) {
        this.edges = new HashMap<>();
        this.pass_number = pass_number;
        this.activity_class = activityclass;
        this.graph_nodes = graph_nodes;
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName(), pass_number);
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
        for (String line : lines) {
            String testString = line.replace("\n", "").replace("\r", "");
            Matcher invokeMatcher = Constants.TARGET_INVOKE_LINE.matcher(testString);
            if (invokeMatcher.find()) {
                Matcher classMatcher = Constants.TARGET_ACTIVITY.matcher(testString);
                if (classMatcher.find()) {
                    if (classMatcher.group(1).equals(callee)) {
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
                    this.edges.put(srcClass.getName(), new ArrayList<>());
                }
                if(!this.edges.get(srcClass.getName()).contains(callee)) {
                    this.edges.get(srcClass.getName()).add(callee);
                }
            }
        }
    }

    /**
     * store unlabeled edges that go from one activity to another
     * @param stmt the invoke stmt
     */
    private void storeGraphEdges(InvokeStmt stmt) {
        SootMethod method = stmt.getInvokeExpr().getMethod();
        SootClass methodClass = method.getDeclaringClass();
        // this.logWriter.write(LogType.OUT, "outer if -> " + stmt.getInvokeExpr().getMethod().getName(), true);
        if(method.isConstructor()
                && method.getParameterCount() == 2
                && method.getParameterType(0).toString().equals(Constants.CONTEXT_CLASS)
                && method.getParameterType(1).toString().contains(Constants.JAVA_CLASS_CLASS)
                && methodClass.getName().equals(Constants.INTENT_CLASS)) {
            // this.logWriter.write(LogType.OUT, "inner if -> " + stmt.getInvokeExpr().getMethod().getName(), true);
            ValueBox valueBox = stmt.getInvokeExprBox();
            Matcher matcher = Constants.TARGET_ACTIVITY.matcher(valueBox.getValue().toString());
            if(matcher.find()) {
                storePossibleCallersIntent(method, matcher.group(1));
            }
        }
    }

    /**
     * statement to catch invoke statements from AbstractStmtSwitch
     * @param stmt the current invoke statement
     */
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        if(stmt.containsInvokeExpr()) {
            storeGraphEdges(stmt);
        }
    }

    /**
     * statement to catch assignment statements from AbstractStmtSwitch
     * @param stmt the current assignment statement
     */
    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        if(stmt.containsInvokeExpr()) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            SootMethod sootMethod = invokeExpr.getMethod();
            // find all of the calls to findViewById() that get assigned to something
            if(sootMethod.getName().equals(Constants.FIND_VIEW_BY_ID_METHOD)) {
                String left_type = stmt.getLeftOp().getType().toString();
                String right_type = stmt.getRightOp().getType().toString();
                String left_value = stmt.getLeftOpBox().getValue().toString();
                String right_value = stmt.getRightOpBox().getValue().toString();
                String msg = String.format("Assignment -> %s, %s  %s, %s", left_type, right_type, left_value, right_value);
                this.logWriter.write(LogType.OUT, msg, true);
            }
        }
    }
}

