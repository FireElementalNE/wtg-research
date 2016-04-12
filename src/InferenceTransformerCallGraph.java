import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Sources;

import java.io.IOException;
import java.util.*;

/**
 * Created by fire on 4/12/16.
 */
public class InferenceTransformerCallGraph extends BodyTransformer {
    private LogWriter logWriter;
    private List<String> nodes;
    private List<String> edges;
    private List<String> UIElements;
    public InferenceTransformerCallGraph() {
        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("InterfaceTransformerCallGraph: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }
    private String makeHashName(SootMethod sootMethod) {
        return sootMethod + ":" + Integer.toString(sootMethod.hashCode());
    }
    private String makeEdgeName(SootMethod a, SootMethod b) {
        // a --> b in the callgraph
        return makeHashName(a) + " --> " + makeHashName(b);
    }
    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        SootMethod sootMethod = body.getMethod();
        CallGraph cg = Scene.v().getCallGraph();
        if(!this.nodes.contains(makeHashName(sootMethod)) && !sootMethod.getName().contains("java")) {
            this.nodes.add(makeHashName(sootMethod));
        }
        Iterator sources = new Sources(cg.edgesInto(sootMethod));
        while (sources.hasNext()) {
            SootMethod src = (SootMethod)sources.next();
            String edge = makeEdgeName(src, sootMethod);
            if(!this.edges.contains(edge)
                    && !sootMethod.getName().contains("java")
                    && !src.getName().contains("java")) {
                this.edges.add(edge);
            }
        }
    }
    public void printGraph() {
        for(String s : this.nodes) {
            this.logWriter.write_out(s);
        }
        for(String s : this.edges) {
            this.logWriter.write_out(s);
        }
    }
}
