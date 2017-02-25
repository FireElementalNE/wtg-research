package androidgraph;

import soot.SootClass;

import java.util.HashMap;

/**
 * Created by fire on 2/25/17.
 */
public class WTGGraphNode {
    private String activity_name;
    private SootClass activity_class;
    private HashMap<String, WTGGraphUIElement> ui_elements;

    /**
     * constructor to create a new Node for the graph
     *
     * @param name       the name of the activity
     * @param soot_class the class instance of the activity
     */
    public WTGGraphNode(String name, SootClass soot_class) {
        this.activity_name = name;
        this.activity_class = soot_class;
        this.ui_elements = new HashMap<>();
    }

    /**
     * get the activity name
     *
     * @return the activity name
     */
    public String get_activity_name() {
        return this.activity_name;
    }

    /**
     * get the the class instance of the activity
     *
     * @return the class instance of the activity
     */
    public SootClass get_activity_class() {
        return this.activity_class;
    }

    /**
     * get a specific ui element from the ui_elements HashMap
     *
     * @param name the name of the ui element
     * @return the WTGGraphUIElement iff it exists in the HashMap otherwise
     * return null
     */
    public WTGGraphUIElement get_ui_element(String name) {
        if (this.ui_elements.containsKey(name)) {
            return this.ui_elements.get(name);
        }
        return null;
    }

    /**
     * add a WTGGraphUIElement to the ui_elements HashMap
     *
     * @param name       the name of the WTGGraphUIElement
     * @param soot_class the class of the WTGGraphUIElement
     */
    public void add_ui_element(String name, SootClass soot_class) {
        this.ui_elements.put(name, new WTGGraphUIElement(name, soot_class));
    }

}
