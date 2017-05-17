import soot.*;

import java.io.IOException;
import java.util.*;


class InferenceTransformer extends BodyTransformer {
    private List<WTGGraphEdge> edges;
    private List<WTGGraphNode> nodes;
    private LogWriter logWriter;
    private Map<String, String> implicit_intents;

    /**
     * Constructor
     */
    InferenceTransformer(Map<String, String> implicit_intents) {
        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.implicit_intents = implicit_intents;

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
            if (Utilities.check_ancestry(methodClass, Constants.ACTIVITY_SUPERCLASS)) {
                if (!Utilities.androidSkip(methodClass)) {
                    try {
                        // find the onCreate method
                        SootMethod on_create = methodClass.getMethodByName(Constants.ON_CREATE_METHOD_NAME);
                        // gets the fields of the activity
                        WTGGraphNode wtgGraphNode = new WTGGraphNode(methodClass.getName(), methodClass, on_create);
                        for (SootField sootField : methodClass.getFields()) {
                            String field_type = sootField.getType().toString();
                            String local = sootField.getName();
                            if (Constants.WIDGET_CHECK.matcher(field_type).find()) {
                                wtgGraphNode.add_ui_element(local, field_type);
                            }
                        }
                        this.logWriter.write_parse(LogType.OUT, wtgGraphNode.toString());
                        this.nodes.add(wtgGraphNode);
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
                    this.logWriter.write_parse(LogType.OUT, msg);
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
        List<WTGGraphEdge> tmp_edges = visitor.edges;
        if(tmp_edges.size() > 0) {
            for (WTGGraphEdge tmp_edge : tmp_edges) {
                boolean found = false;
                for (WTGGraphEdge edge : this.edges) {
                    if (tmp_edge.get_name().equals(edge.get_name())) {
                        List<String> targets = tmp_edge.get_targets();
                        for (String target : targets) {
                            edge.add_target(target);
                        }
                        found = true;
                    }
                }
                if (!found) {
                    this.edges.add(tmp_edge);
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
        InferenceVisitor visitor = new InferenceVisitor(this.implicit_intents);
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
     * prints ui elements (with activity name and counts)
     */
    public void print_ui_elements() {
        for(WTGGraphNode wtgGraphNode : this.nodes) {
            this.logWriter.write_parse(LogType.SCR, wtgGraphNode.get_activity_name());
            HashMap<String, Integer> counts = wtgGraphNode.get_counts();
            for(HashMap.Entry<String, Integer> entry : counts.entrySet()) {
                this.logWriter.write_parse(LogType.SCR, String.format("\t%s: %d", entry.getKey(), entry.getValue()));
            }
        }
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
        for (WTGGraphNode entry : this.nodes) {
            this.logWriter.write_no_parse(LogType.OUT, "Activity: " + entry.get_activity_name());
        }
    }

    /**
     * Print graph edges
     */
    private void printEdges() {
        this.logWriter.write_parse(LogType.OUT, "writing edges " + Integer.toString(this.edges.size()));
        for (WTGGraphEdge edge : this.edges) {
            List<String> targets = edge.get_targets();
            this.logWriter.write_parse(LogType.OUT, "Edge target size: " + Integer.toString(targets.size()));
            for(String target : targets) {
                String out_str = String.format("%s --> %s", edge.get_name(), target);
                this.logWriter.write_no_parse(LogType.OUT, out_str);
            }
        }
        this.logWriter.write_parse(LogType.OUT, "done writing edges " + Integer.toString(this.edges.size()));
    }

    /**
     * gets the graph nodes
     * @return the graph nodes
     */
    public List<WTGGraphNode> get_nodes() {
        return nodes;
    }

    /**
     * get the graph edges
     * @return the graph edges
     */
    public List<WTGGraphEdge> get_edges() {
        return edges;
    }
}
