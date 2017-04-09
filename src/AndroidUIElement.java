import soot.SootClass;

/**
 * Created by fire on 3/11/17.
 */

public class AndroidUIElement {
    private String type;
    private String id;
    private String XML_filename;
    private SootClass element_class;

    /**
     * Constuctor for basic AndroidUIElement
     * @param id id as defined in XML
     * @param type type as defined in XML
     * @param XML_filename the XML file that this UI element came from
     */
    public AndroidUIElement(String id, String type, String XML_filename) {
        this.type = type;
        this.id = id;
        this.XML_filename = XML_filename;
    }

    /**
     * get the XML filename that the UI element came from
     * @return the XML filename
     */
    public String getXML_filename() {
        return XML_filename;
    }

    /**
     * get the id of the UI element
     * @return the id of the UI element
     */
    public String getId() {
        return id;
    }

    /**
     * get the type of the UI element
     * @return the type of the UI element
     */
    public String getType() {
        return type;
    }

    /**
     * stringify the AndroidUIElement
     * @return a String representing the AndroidUIElement
     */
    public String toString() {
        return this.type + " " + this.id + " " + this.XML_filename;
    }

    /**
     * set the element class of the UI element
     * @param element_class the element class
     */
    public void set_element_class(SootClass element_class) {
        this.element_class = element_class;
    }
}
