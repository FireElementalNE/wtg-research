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
    List<String> UIElements;
    List<SootClass> onClickListeners;
    private int passNumber;

    /**
     * Constructor for first pass
     * @param passNumber the pass number
     */
    InferenceVisitor(int passNumber) {
        this.UIElements = new ArrayList<>();
        this.edges = new HashMap<>();
        this.onClickListeners = new ArrayList<>();
        this.passNumber = passNumber;
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName(), passNumber);
        } catch (IOException e) {
            System.err.println("InferenceVisitor: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructor for the second pass
     * @param passNumber the pass number
     * @param onClickListeners the list of onclicklisteners from the first pass
     */
    InferenceVisitor(int passNumber, List<SootClass> onClickListeners) {
        this.passNumber = passNumber;
        this.onClickListeners = onClickListeners;
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName(), passNumber);
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
                    this.edges.put(srcClass.getName(), new ArrayList<String>());
                }
                if(!this.edges.get(srcClass.getName()).contains(callee)) {
                    this.edges.get(srcClass.getName()).add(callee);
                }
            }
        }
    }
    /**
     * TODO: Fix this method
     * Trys to find the UI element calling setOnClickListner
     * @param stmt the current invoke stmt
     */
    private void getUIElement(InvokeStmt stmt) {
        // TODO: Get UI element
        SootMethod method = stmt.getInvokeExpr().getMethod();
        SootClass methodClass = method.getDeclaringClass();

        if(method.getName().contains(Constants.SET_ONCLICK_LISTNER)) {
            if(method.hasActiveBody()) {
                this.logWriter.write(LogType.SCR, method.getName() + " has active body.");
                InvokeExpr invokeExpr = stmt.getInvokeExpr();
                this.logWriter.write(LogType.SCR, "Method: " + method.toString());
                // SpecialInvokeExpr specialInvokeExpr = (SpecialInvokeExpr) invokeExpr;
                VirtualInvokeExpr virtualInvokeExpr = (VirtualInvokeExpr) invokeExpr;
                InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
                Value value = virtualInvokeExpr.getBase();
                this.logWriter.write(LogType.SCR, "The Type virtualInvokeExpr -- > " + virtualInvokeExpr.getType().toString());
                this.logWriter.write(LogType.SCR, "The Type instanceInvokeExpr -- > " + instanceInvokeExpr.getType().toString());
                // InterfaceInvokeExpr interfaceInvokeExpr = (InterfaceInvokeExpr) invokeExpr;
                this.logWriter.write(LogType.SCR, "VirtualInvokeExpr (BASE) THE TYPE? --> " + virtualInvokeExpr.getBase().getType().toString());
                // this.logWriter.writeScratch("SpecialInvokeExpr THE TYPE? --> " + specialInvokeExpr.getBase().getType().toString());
                this.logWriter.write(LogType.SCR, "InstanceInvokeExpr (BASE) THE TYPE? --> " + instanceInvokeExpr.getBase().getType().toString());
                // this.logWriter.writeScratch("InterfaceInvokeExpr THE TYPE? --> " + interfaceInvokeExpr.getBase().getType().toString());
                // TODO: Need to get the button (or whatever)
                // Have to find the _button_ in: button.setOnClickListner()
                // We have a list of OnClickListners at this point (this is the second pass)
            }
        }
    }

    /**
     * Store onclick listeners on the _first_ pass so that they can be used
     * on the _second_ pass
     * @param stmt the InvokeStmt currently being analyzed
     */
    private void storeOnClickListenerInvokeStmt(InvokeStmt stmt) {
        SootMethod method = stmt.getInvokeExpr().getMethod();
        SootClass methodClass = method.getDeclaringClass();
        if(method.isConstructor()
                && method.getParameterCount() == 1
                && Utilities.checkInterfaces(methodClass, Constants.ON_CLICK_LISTENER_CLASS)
                && method.hasActiveBody()
                && !Utilities.androidSkip(methodClass)) {
            //this.logWriter.write(LogType.OUT, methodClass.getName());
            List <SootMethod> methods = methodClass.getMethods();
            this.logWriter.write(LogType.OUT,"\tNumber of methods found  in "
                    + methodClass.getName()
                    + " (storeOnClickListenerInvokeStmt): " + methods.size());
            try {
                SootMethod onClickMethod = methodClass.getMethodByName(Constants.ONCLICK);
                if (onClickMethod.hasActiveBody()) {
                    this.logWriter.write(LogType.OUT, "Found onClick, and it has an active body.");
                    // this.logWriter.writeOut(onClickMethod.getActiveBody().toString());
                } else {
                    this.logWriter.write(LogType.ERR, "Found onClick, but it has no active body.");
                }
            } catch (RuntimeException rte) {
                if (Constants.PRINT_ST) {
                    rte.printStackTrace();
                }
                this.logWriter.write(LogType.ERR, "Did not find onClick in class " + methodClass.getName() + ".");
                this.logWriter.write(LogType.ERR, "\t" + rte.getMessage());
            }
            this.onClickListeners.add(methodClass);

        }
    }


    /**
     * store unlabeled edges that go from one activity to another
     * @param stmt the invoke stmt
     */
    private void storeGraphEdges(InvokeStmt stmt) {
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

    /**
     * Overidden statement to catch invoke statements
     * @param stmt the current invoke statement
     */
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {

        if(stmt.containsInvokeExpr()) {
            if(this.passNumber == 1) {
                storeGraphEdges(stmt);
                storeOnClickListenerInvokeStmt(stmt);
            }
            else {
                getUIElement(stmt);
            }
        }
    }
}
