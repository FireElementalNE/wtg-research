package androidgraph;

import soot.SootClass;
import soot.SootMethod;

import java.util.HashMap;

/**
 * Created by fire on 2/25/17.
 */
public class GraphUIElement {
    private String button_name;
    private SootClass button_class;
    private HashMap<String, SootMethod> listners;
    private HashMap<String, SootMethod> callbacks;

    /**
     * Constuctor to create a Graphbutton without knowing it's soot_class
     * @param name the variable (or local) name of the ui element
     */
    public GraphUIElement(String name) {

    }


}
