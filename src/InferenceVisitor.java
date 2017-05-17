import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Sources;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class InferenceVisitor extends AbstractStmtSwitch {
    private LogWriter logWriter;
    public List<WTGGraphEdge> edges;
    private Map<String, String> implicit_intents;
    /**
     * Constructor Visitor
     * @param implicit_intents a map mapping intent filters strings  to activities
     */
    InferenceVisitor(Map<String, String> implicit_intents) {
        this.edges = new ArrayList<>();
        this.implicit_intents = implicit_intents;
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
     * @param invoke_line the pattern that matches the given invoke line
     * @param activity_matcher the pattern the takes the invoke line and finds the callee
     * @param is_implicit boolean true iff we are calling with an implicit intent
     * @return true iff the callee is being invoked
     */
    private boolean check_intent_callEdge(String activityBody, String callee, Pattern invoke_line, Pattern activity_matcher, boolean is_implicit) {
        String lines[] = activityBody.split("\\r?\\n");
        for (String line : lines) {
            String testString = line.replace("\n", "").replace("\r", "");
            Matcher invokeMatcher = invoke_line.matcher(testString);
            if (invokeMatcher.find()) {
                Matcher classMatcher = activity_matcher.matcher(testString);
                if (classMatcher.find()) {
                    if(is_implicit) {
                        String tmp = search_implicit_intents(classMatcher.group(1));
                        if (callee.equals(tmp) && !tmp.equals("")) {
                            return true;
                        }

                    }
                    else {
                        if (classMatcher.group(1).equals(callee)) {
                            return true;
                        }
                    }


                }
            }
        }
        return false;
    }


    /**
     * change the edges
     * @param srcClass the activity that we are leaving
     * @param callee the activity we are going to
     */
    private void modify_edges(SootClass srcClass, String callee) {
        boolean found = false;
        this.logWriter.write_no_parse(LogType.SCR, "Got here! 4. " + callee);
        for(int i = 0; i < this.edges.size(); i++) {
            if(this.edges.get(i).get_name().equals(srcClass.getName())) {
                this.logWriter.write_parse(LogType.OUT, "found edge " + srcClass.getName() + " -> " + callee);
                this.edges.get(i).add_target(callee);
                found = true;
            }
        }
        if(!found) {
            this.logWriter.write_parse(LogType.OUT, "made new edge "  + srcClass.getName() + " -> " + callee);
            WTGGraphEdge wtgGraphEdge = new WTGGraphEdge(srcClass);
            wtgGraphEdge.add_target(callee);
            this.edges.add(wtgGraphEdge);
        }
    }

    /**
     * Stores possible Intent callers, prelim to creating an edge
     * @param target the method that has the intent call
     * @param callee the target Activity of the intent call
     * @param invoke_line the pattern to match the invoke line
     * @param activity_matcher the pattern the takes the invoke line and finds the callee
     * @param is_implicit boolean true iff we are calling with an implicit intent
     */
    private void store_possible_callers_intent(SootMethod target, String callee, Pattern invoke_line, Pattern activity_matcher, boolean is_implicit) {
        CallGraph cg = Scene.v().getCallGraph();
        Iterator sources = new Sources(cg.edgesInto(target));
        while (sources.hasNext()) {
            SootMethod src = (SootMethod)sources.next();
            SootClass srcClass = src.getDeclaringClass();
            // TODO: make this more concrete, pretty hackish
            // TODO: fix made it a _bit_ less hackish but still pretty hackish
            // TODO: issue remains
            if(check_intent_callEdge(src.getActiveBody().toString(), callee, invoke_line, activity_matcher, is_implicit)) {
                modify_edges(srcClass, callee);
            }
        }
    }

    /**
     * searches the implicit intent filters to find the correct activity
     * @param filter_string the implicit intent filter
     * @return the activity or the empty string if no implicit intent filter is found
     */
    public String search_implicit_intents(String filter_string) {
        String callee = "";
        for(Map.Entry<String, String> entry : this.implicit_intents.entrySet()) {
            if(entry.getKey().equals(filter_string)) {
                callee = entry.getValue();
                callee = callee.replace('\\','.');
            }
        }
        return callee;
    }

    /**
     * store unlabeled edges that go from one activity to another
     * @param stmt the invoke stmt
     */
    private void store_graph_edges(InvokeStmt stmt) {
        SootMethod method = stmt.getInvokeExpr().getMethod();
        SootClass methodClass = method.getDeclaringClass();
        if(method.isConstructor()
                && method.getParameterCount() == 2
                && method.getParameterType(0).toString().equals(Constants.CONTEXT_CLASS)
                && method.getParameterType(1).toString().contains(Constants.JAVA_CLASS_CLASS)
                && methodClass.getName().equals(Constants.INTENT_CLASS)) {
            ValueBox valueBox = stmt.getInvokeExprBox();
            Matcher matcher = Constants.TARGET_ACTIVITY_EXPLICIT.matcher(valueBox.getValue().toString());
            if(matcher.find()) {
                store_possible_callers_intent(method, matcher.group(1), Constants.TARGET_INVOKE_LINE_EXPLICIT, Constants.TARGET_ACTIVITY_EXPLICIT, false);
            }
        }
        else if(method.isConstructor()
                && method.getParameterCount() == 1
                && method.getParameterType(0).toString().equals(Constants.JAVA_STRING_CLASS)
                && methodClass.getName().equals(Constants.INTENT_CLASS)) {
            ValueBox valueBox = stmt.getInvokeExprBox();
            Matcher matcher = Constants.TARGET_ACTIVITY_IMPLICIT.matcher(valueBox.getValue().toString());
            if(matcher.find()) {
                String callee = search_implicit_intents(matcher.group(1));
                store_possible_callers_intent(method, callee, Constants.TARGET_INVOKE_LINE_IMPLICIT, Constants.TARGET_ACTIVITY_IMPLICIT, true);
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
            store_graph_edges(stmt);
        }
    }
}

