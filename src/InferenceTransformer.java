import soot.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;


public class InferenceTransformer extends BodyTransformer {
    private Map <String, List<String>> edges;
    private List<String> nodes;
    private List<String> UIElements;
    public LogWriter logWriter;

    /**
     * Constructor
     */
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

    /**
     * Checks a class heirarchy to see if it is decended from android.app.Activity
     * does this recursivly
     * @param sootClass the current class being checked
     * @return a boolean that is true iff the current class IS android.app.Activity
     */
    private boolean checkActivity(SootClass sootClass) {
        if(!sootClass.hasSuperclass()) {
            return false;
        }
        else if(sootClass.hasSuperclass()) {
            SootClass methodSuperclass = sootClass.getSuperclass();
            if(methodSuperclass.getName().equals(Constants.ACTIVITY_SUPERCLASS)) {
                return true;
            }
            else {
                return checkActivity(methodSuperclass);
            }
        }
        return false;
    }

    /**
     * TODO: Fix this method
     * Trys to find an onclick from an onclick lisnter method
     * @param methodClass the current class calling the current method (setOnClickListener)
     * @param method the current method (setOnClickListener)
     */
    private void getOnClickMethodFromListner(SootClass methodClass, SootMethod method) {
        // TODO: Get UI element
        // Idea:
        //  1. Find onclick listner get the first arg which will be of type View.OnClickListener()
        //  2. Get the onClick method of that listner
        //  3. ...
        if(method.getName().equals(Constants.SET_ONCLICK_LISTNER)) {
            try {
                SootMethod onClickMethod = methodClass.getMethodByName(Constants.ONCLICK);
                if (onClickMethod.hasActiveBody()) {
                    this.logWriter.writeScratch(onClickMethod.getActiveBody().toString());
                } else {
                    this.logWriter.writeScratch("No active body.");
                }
            } catch (RuntimeException rte) {
                if (Constants.PRINT_ST) {
                    rte.printStackTrace();
                }
                this.logWriter.writeErr(rte.getMessage());
            }
        }
    }

    /**
     * looks for activities to create nodes in the graph
     * @param methodClass the current class calling the current method
     * @param method the current method
     */
    private void checkForActivities(SootClass methodClass, SootMethod method) {
        // this finds all of the custom activities (activities that do not start with android.<something>)
        if(methodClass.hasSuperclass() && method.isConstructor()) {
            // SootClass method_superclass = method_class.getSuperclass();
            if (checkActivity(methodClass)) {
                Matcher matcher = Constants.ANDROID_SKIP.matcher(methodClass.getName());
                if (!matcher.find()) {
                    this.nodes.add(methodClass.getName());
                } else {
                    String msg = "Skipped: " + methodClass.getName();
                    this.logWriter.writeOut(msg);
                }
            }
        }
    }

    /**
     * Updates the UI element list, not currently used
     * @param visitor the InferenceVisitor that holds the new UIElements
     */
    private void updateUIElements(InferenceVisitor visitor) {
        for(String el : visitor.UIElements) {
            this.UIElements.add(el);
        }
    }

    /**
     * Updates the edges list after the InferenceVisitor runs through the method
     * @param visitor the InferenceVisitor that holds the new edges
     */
    private void updateEdges(InferenceVisitor visitor) {
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
    }

    /**
     * Send the current body through the InferenceVisitor
     * @param body the current body being analyzed
     */
    private void sendToVisitor(Body body) {
        final PatchingChain<Unit> units = body.getUnits();
        InferenceVisitor visitor = new InferenceVisitor();
        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
            final Unit u = iter.next();
            u.apply(visitor);
        }
        updateEdges(visitor);
        updateUIElements(visitor);
    }

    /**
     * Overidden internalTransform method, gets called for every body.
     * @param body the body
     * @param phaseName phase
     * @param options options map
     */
    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        SootMethod method = body.getMethod();
        SootClass methodClass = method.getDeclaringClass();
        checkForActivities(methodClass, method);
        // See comments on getOnClickMethodFromListner()
        // getOnClickMethodFromListner(methodClass, method);
        // send to InferenceVisitor
        sendToVisitor(body);
    }

    /**
     *  Print everything
     */
    public void printAll() {
        printNodes();
        printEdges();
        printUIElements();
    }

    /**
     * Print graph nodes
     */
    private void printNodes() {
        for(String entry : this.nodes) {
            this.logWriter.writeOut("Activity: " + entry);
        }
    }

    /**
     * Print graph edges
     */
    private void printEdges() {
        for(Map.Entry<String, List<String>> entry : this.edges.entrySet()) {
            for(String string : entry.getValue()) {
                this.logWriter.writeOut(entry.getKey() + " --> " + string);
            }
        }
    }

    /**
     * Print UI elements
     */
    private void printUIElements() {
        for(String el : this.UIElements) {
            this.logWriter.writeScratch(el);
        }
    }
}
