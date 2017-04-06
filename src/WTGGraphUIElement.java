import soot.SootMethod;

import java.util.HashMap;

/**
 * Created by fire on 2/25/17.
 */
public class WTGGraphUIElement {
    private String element_name;
    private String element_class;
    private HashMap<String, SootMethod> listeners;
    private HashMap<String, SootMethod> callbacks;

    /**
     * constructor to create a WTGGraphUIElement
     * @param name the variable (or local) name of the ui element
     * @param view_class the ui element class (Button, RadioButton ect)
     */
    public WTGGraphUIElement(String name, String view_class) {
        this.element_name = name;
        this.element_class = view_class;
        this.listeners = new HashMap<>();
        this.callbacks = new HashMap<>();
    }

    /**
     * get the name of the ui element
     *
     * @return the name of the ui element
     */
    public String get_element_name() {
        return this.element_name;
    }

    /**
     * get the element class
     *
     * @return the element class
     */
    public String get_element_class() {
        return this.element_class;
    }

    /**
     * add a listener to the listener hashmap
     *
     * @param name        the name of the listener
     * @param soot_method the SootMethod of the listener
     */
    public void add_listner(String name, SootMethod soot_method) {
        this.listeners.put(name, soot_method);
    }

    /**
     * add a callback to the callback hashmap
     *
     * @param name        the name of the callback
     * @param soot_method the SootMethod of the callback
     */
    public void add_callback(String name, SootMethod soot_method) {
        this.callbacks.put(name, soot_method);
    }

    /**
     * get the SootMethod of a specific listener
     *
     * @param name the name of the listener
     * @return the SootMethod iff the name exists in the listener HashMap
     */
    public SootMethod get_listener(String name) {
        if (this.listeners.containsKey(name)) {
            return this.listeners.get(name);
        }
        return null;
    }

    /**
     * get the SootMethod of a specific callback
     *
     * @param name the name of the callback
     * @return the SootMethod iff the name exists in the callback HashMap
     * otherwise return null
     */
    public SootMethod get_callback(String name) {
        if (this.callbacks.containsKey(name)) {
            return this.callbacks.get(name);
        }
        return null;
    }


}
