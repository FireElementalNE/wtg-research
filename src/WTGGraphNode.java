import soot.Body;
import soot.SootClass;
import soot.SootMethod;

import java.util.HashMap;

/**
 * Created by fire on 2/25/17.
 */
public class WTGGraphNode {
    private String activity_name;
    private SootClass activity_class;
    private SootMethod on_create_method;
    private Body on_create_body;
    private boolean has_on_create_active_body;
    private HashMap<String, WTGGraphUIElement> ui_elements;

    /**
     * constructor to create a new Node for the graph
     * @param name the name of the activity
     * @param soot_class the class instance of the activity
     * @param on_create_method the onCreate() method of the activity
     */
    public WTGGraphNode(String name, SootClass soot_class, SootMethod on_create_method) {
        this.activity_name = name;
        this.activity_class = soot_class;
        this.on_create_method = on_create_method;
        if(this.on_create_method.hasActiveBody()) {
            this.has_on_create_active_body = true;
            this.on_create_body = this.on_create_method.getActiveBody();
        }
        else {
            this.has_on_create_active_body = false;
        }
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
     * @param view_class the class of the WTGGraphUIElement
     */
    public void add_ui_element(String name, String view_class) {
        this.ui_elements.put(name, new WTGGraphUIElement(name, view_class));
    }

    /**
     * get the onCreate() method of the activity
     * @return the onCreate method of the activity
     */
    public SootMethod get_on_create_method() {
        return on_create_method;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.activity_name);
        sb.append(" => ");
        for(HashMap.Entry<String, WTGGraphUIElement> entry: this.ui_elements.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * get counts for each UI type
     * @return a hashmap with the key being the class and the value being the number
     * of times that class is declared in this Node
     */
    public HashMap<String, Integer> get_counts() {
        HashMap<String, Integer> ret_val = new HashMap<>();
        for(HashMap.Entry<String, WTGGraphUIElement> entry : this.ui_elements.entrySet()) {
            String classname = entry.getValue().get_element_class();
            if(ret_val.containsKey(classname)) {
                ret_val.replace(classname, ret_val.get(classname) + 1);
            }
            else {
                ret_val.put(classname, 1);
            }
        }
        return ret_val;
    }

    /**
     * return ths boolean has_on_create_active_body
     * @return true iff the oncreate method has created an active body
     */
    public boolean has_active_body() {
        return this.has_on_create_active_body;
    }

    /**
     * get the onCreate method body
     * @return the onCreate method body
     */
    public Body get_on_create_body() {
        return on_create_body;
    }

}
