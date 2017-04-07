import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

import java.io.IOException;
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
     * attempts to link the the XML ui elements with the found UI elements
     * this is a first step
     * TODO: continue to expand this, use nodes and edges
     */
    public void find_ui_element_class() {
        Chain<SootClass> classes = this.scene.getClasses();
        for(SootClass sootClass : classes) {
            if(sootClass.getName().endsWith(Constants.RID_CLASS_ENDING) &&
                    !Constants.ANDROID_SKIP.matcher(sootClass.getName()).find()) {
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
