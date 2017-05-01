import soot.*;
import soot.util.Chain;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fire on 4/6/17.
 */
public class WTGGraph {
    private List<WTGGraphNode> nodes;
    private List<WTGGraphEdge> edges;
    private List <AndroidUIElement> ui_elements;
    private Scene scene;
    private LogWriter logWriter;

    /**
     * Constructor
     */
    WTGGraph(List <WTGGraphNode> nodes, List<WTGGraphEdge> edges, Scene scene, AndroidXMLUtility androidXMLUtility) {
        this.nodes = nodes;
        this.edges = edges;
        this.scene = scene;
        this.ui_elements = androidXMLUtility.get_android_UIElements();
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("WTGGraph: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    /**
     * a temporary function to examine the nodes
     */
    public void examine_nodes() {
        for(WTGGraphNode wtgGraphNode : this.nodes) {
            if(wtgGraphNode.has_active_body()) {
                this.logWriter.write(LogType.OUT, "node oncreate has an active body (" + wtgGraphNode.get_activity_name() + ")", true);
                final PatchingChain<Unit> units = wtgGraphNode.get_on_create_body().getUnits();
                UIInferenceVisitor visitor = new UIInferenceVisitor();
                for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
                    final Unit u = iter.next();
                    u.apply(visitor);
                }
            }
            else {
                this.logWriter.write(LogType.OUT, "node oncreate has NO active body (" + wtgGraphNode.get_activity_name() + ")", true);

            }
        }
    }


    /**
     * attempts to link the the XML ui elements with the found UI elements
     * this is a first step
     * TODO: continue to expand this, use nodes and edges
     */
    public void find_ui_element_class() {
        Chain<SootClass> classes = this.scene.getClasses();
        for(SootClass sootClass : classes) {
            if(sootClass.getName().endsWith(Constants.RID_CLASS_ENDING) &&
                    !Constants.ANDROID_SKIP.matcher(sootClass.getName()).find()) {
                for(SootMethod sootMethod : sootClass.getMethods()) {
                    // TODO:Fix this so i can pass to visitor. I need a body to do that
                    /* MethodSource methodSource = sootMethod.getSource();
                    Body body = sootMethod.retrieveActiveBody();
                    final PatchingChain<Unit> units = body.getUnits();
                    GraphInferenceVisitor visitor = new GraphInferenceVisitor();
                    for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
                        final Unit u = iter.next();
                        u.apply(visitor);
                    }*/
                    this.logWriter.write(LogType.OUT, sootMethod.getName(), true);
                }
                this.logWriter.write(LogType.OUT, sootClass.getName(), true);
                Chain<SootField> fields = sootClass.getFields();
                for(SootField field : fields) {
                    for(AndroidUIElement ui_element : this.ui_elements) {
                        if(ui_element.getId().contains(field.getName())) {
                            String out_str = String.format("\t%s | %s | %s", field.getName(), ui_element.getId(), ui_element.getXML_filename());
                            this.logWriter.write(LogType.OUT, out_str, true);
                        }
                    }
                }
            }
        }
    }
}
