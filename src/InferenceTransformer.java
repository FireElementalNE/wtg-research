import soot.*;

import java.io.IOException;
import java.util.*;


class InferenceTransformer extends BodyTransformer {
    private Map <String, List<String>> edges;
    private List<String> nodes;
    private List<String> UIElements;
    private LogWriter logWriter;
    /**
     * Constructor
     */
    InferenceTransformer() {
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
     * looks for activities to create nodes in the graph
     * @param methodClass the current class calling the current method
     * @param method the current method
     */
    private void checkForActivities(SootClass methodClass, SootMethod method) {
        // this finds all of the custom activities (activities that do not start with android.<something>)
        if(methodClass.hasSuperclass() && method.isConstructor()) {
            // SootClass method_superclass = method_class.getSuperclass();
            if (Utilities.checkAncestry(methodClass, Constants.ACTIVITY_SUPERCLASS)) {
                if (!Utilities.androidSkip(methodClass)) {
                    this.nodes.add(methodClass.getName());
                } else {
                    String msg = "Skipped: " + methodClass.getName();
                    this.logWriter.write(LogType.OUT, msg);
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
    private void sendToVisitorFirstPass(Body body) {
        final PatchingChain<Unit> units = body.getUnits();
        InferenceVisitor visitor = new InferenceVisitor(1);
        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
            final Unit u = iter.next();
            u.apply(visitor);
        }
        updateEdges(visitor);
        updateUIElements(visitor);
        sendToVisitorSecondPass(body, visitor);
    }

    /**
     * do second visit pass to find correct OnClickListner Declarations
     * @param body the current body being analyzed
     * @param visitor the visitor from the first pass
     */
    private void sendToVisitorSecondPass(Body body, InferenceVisitor visitor) {
        final PatchingChain<Unit> units = body.getUnits();
        InferenceVisitor visitorSecondPass = new InferenceVisitor(2, visitor.onClickListeners);
        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
            final Unit u = iter.next();
            u.apply(visitorSecondPass);
        }
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
        sendToVisitorFirstPass(body);
    }

    /**
     *  Print everything
     */
    void printAll() {
        printNodes();
        printEdges();
        printUIElements();
    }

    /**
     * Print graph nodes
     */
    private void printNodes() {
        for(String entry : this.nodes) {
            this.logWriter.write(LogType.OUT, "Activity: " + entry);
        }
    }

    /**
     * Print graph edges
     */
    private void printEdges() {
        for(Map.Entry<String, List<String>> entry : this.edges.entrySet()) {
            for(String string : entry.getValue()) {
                this.logWriter.write(LogType.OUT, entry.getKey() + " --> " + string);
            }
        }
    }

    /**
     * Print UI elements
     */
    private void printUIElements() {
        for(String el : this.UIElements) {
            this.logWriter.write(LogType.SCR, el);
        }
    }
}
