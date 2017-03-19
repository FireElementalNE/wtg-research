import androidgraph.WTGGraphNode;
import soot.*;

import java.io.IOException;
import java.util.*;


class InferenceTransformer extends BodyTransformer {
    private Map <String, List<String>> edges;
    private List<WTGGraphNode> graph_nodes;
    private LogWriter logWriter;

    /**
     * Constructor
     */
    InferenceTransformer() {
        this.edges = new HashMap<>();
        this.graph_nodes = new ArrayList<>();

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
    private boolean checkForActivities(SootClass methodClass, SootMethod method) {
        // this finds all of the custom activities (activities that do not start with android.<something>)
        if(methodClass.hasSuperclass() && method.isConstructor()) {
            if (Utilities.checkAncestry(methodClass, Constants.ACTIVITY_SUPERCLASS)) {
                if (!Utilities.androidSkip(methodClass)) {
                    try {
                        SootMethod on_create = methodClass.getMethodByName(Constants.ON_CREATE_METHOD_NAME);
                        WTGGraphNode wtgGraphNode = new WTGGraphNode(methodClass.getName(), methodClass, on_create);
                        for (SootField sootField : methodClass.getFields()) {
                            String field_type = sootField.getType().toString();
                            String local = sootField.getName();
                            if (Constants.WIDGET_CHECK.matcher(field_type).find()) {
                                wtgGraphNode.add_ui_element(local, field_type);
                            }
                        }
                        this.logWriter.write(LogType.OUT, wtgGraphNode.toString(), false);
                        this.graph_nodes.add(wtgGraphNode);
                        return true;
                    } catch (RuntimeException e) {
                        System.err.println("InferenceTransformer: could not find onClick() method");
                        if(Constants.PRINT_ST) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                } else {
                    String msg = "Skipped: " + methodClass.getName();
                    this.logWriter.write(LogType.OUT, msg, true);
                    return false;
                }
            }
            return false;
        }
        return false;
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
                } else {
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
        SootClass current_class = body.getMethod().getDeclaringClass();
        InferenceVisitor visitor = new InferenceVisitor(current_class, this.graph_nodes, 1);
        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
            final Unit u = iter.next();
            u.apply(visitor);
        }
        updateEdges(visitor);
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
    }

    /**
     * Print graph nodes
     */
    private void printNodes() {
        for (WTGGraphNode entry : this.graph_nodes) {
            this.logWriter.write(LogType.OUT, "Activity: " + entry.get_activity_name(), false);
        }
    }

    /**
     * Print graph edges
     */
    private void printEdges() {
        for (Map.Entry<String, List<String>> entry : this.edges.entrySet()) {
            for (String string : entry.getValue()) {
                this.logWriter.write(LogType.OUT, entry.getKey() + " --> " + string, false);
            }
        }
    }
}
