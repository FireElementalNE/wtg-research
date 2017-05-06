import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by fire on 3/7/17.
 */
public class AndroidXMLUtility {
    private String apk_path;
    private LogWriter logWriter;

    private List<AndroidUIElement> androidUIElements;
    private Map<String, String> implicit_intents;
    /**
     * Constructor for the AndroidXMLUtility
     * @param apk the file path for the APK
     */
    public AndroidXMLUtility(String apk) {
        this.apk_path = apk;
        this.androidUIElements = new ArrayList<>();
        this.implicit_intents = new HashMap<>();
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("AndroidXMLUtility: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    /**
     * parse a single element and all of its children recursivly
     * @param node the element
     * @param filename the filename that the element was found in
     */
    private void parse_node_element(Node node, String filename) {
        if(node.hasAttributes()) {
            String ui_type = node.getNodeName();
            NamedNodeMap node_child_attrs = node.getAttributes();
            for(int j = 0; j < node_child_attrs.getLength(); j++) {
                Node child_attrs = node_child_attrs.item(j);
                if(child_attrs.getNodeName().equals(Constants.XML_ID_TAG)) {
                    String msg = String.format("%s is %s (%s)", child_attrs.getNodeValue(), ui_type, filename);
                    this.logWriter.write(LogType.OUT, msg, false);
                    this.androidUIElements.add(new AndroidUIElement(child_attrs.getNodeValue(), ui_type, filename));
                }
            }
        }
        if(node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            for(int i = 0; i < children.getLength(); i++) {
                Node next = children.item(i);
                parse_node_element(next, filename);
            }
        }
    }

    /**
     * search a node for a child with a specific tyoe
     * @param node the node
     * @param name the type of the child
     * @return the child with that type or null
     */
    private Node search_children(Node node, String name) {
        NodeList node_children = node.getChildNodes();
        for(int i = 0; i < node_children.getLength(); i++) {
            Node node_child = node_children.item(i);
            if(node_child.getNodeName().equals(name)) {
                return node_child;
            }
        }
        return null;
    }

    /**
     * search a node for multiple children of the same type
     * @param node the node
     * @param name the name of the type of child
     * @return a list of child nodes or null
     */
    private List<Node> search_children_mult(Node node, String name) {
        List <Node> ret = new ArrayList<>();
        NodeList node_children = node.getChildNodes();
        for(int i = 0; i < node_children.getLength(); i++) {
            Node node_child = node_children.item(i);
            if(node_child.getNodeName().equals(name)) {
                ret.add(node_child);
            }
        }
        return ret;
    }

    /**
     * search a node for a specific attribute
     * @param node the node
     * @param key the name of the attribute
     * @return the value of the attribute or null
     */
    private String search_attributes(Node node, String key) {
        NamedNodeMap node_atts = node.getAttributes();
        for(int i = 0; i < node_atts.getLength(); i++) {
            Node attr = node_atts.item(i);
            if(attr.getNodeName().equals(key)) {
                return attr.getNodeValue();
            }
        }
        return null;
    }

    /**
     * search the manifest for implicit intent filters
     * @param manifest the manifest FILE
     */
    private void parse_manifest_for_implicit_intents(File manifest) {
        if(manifest.exists()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(manifest);
                Element root = doc.getDocumentElement();
                Node application = search_children(root, "application");
                if(application != null) {
                    List<Node> activities = search_children_mult(application, "activity");
                    if(activities.size() != 0) {
                        for (Node activity : activities) {
                            String activity_name = search_attributes(activity, "android:name");
                            if(activity_name != null) {
                                Node intent_filter = search_children(activity, "intent-filter");
                                if (intent_filter != null) {
                                    Node action = search_children(intent_filter, "action");
                                    if (action != null) {
                                        String value = search_attributes(action, "android:name");
                                        if(value != null) {
                                            this.logWriter.write(LogType.OUT, "Implicit intent string: " + activity_name + " " + value, false);
                                            this.implicit_intents.put(activity_name, value);
                                        }
                                        else {
                                            this.logWriter.write(LogType.ERR, activity_name + ": action returned null for android:name", false);
                                        }
                                    } else {
                                        this.logWriter.write(LogType.ERR, activity_name + ": I got no action from intent-filter.", false);
                                    }
                                } else {
                                    this.logWriter.write(LogType.ERR, activity_name + " gave me null when I searched for intent-filter.", false);
                                }
                            }
                            else {
                                this.logWriter.write(LogType.ERR, "Got null for android:name in activity", false);
                            }
                        }
                    }
                    else {
                        this.logWriter.write(LogType.ERR, "I got no activity objects from application.", false);
                    }
                }
                else {
                    this.logWriter.write(LogType.ERR, "I get null when I search for application in manifest.", false);
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.err.println("AndroidXMLUtility: parse_manifest_for_implicit_intents " + manifest + " failed.");
                if(Constants.PRINT_ST) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * parse a given XML file in the apk
     * @param filename the filename (and path) of the XML file within the APK
     */
    public void parse_xml_file(String filename)  {
        String[] s = this.apk_path.split("/");
        for(String s1 : s) {
            logWriter.write(LogType.OUT, s1, false);
        }
        String true_filename = Constants.APK_UNPACK_PREFIX + filename;
        File in_file = new File(true_filename);
        if(true_filename.contains("AndroidManifest")) {
            parse_manifest_for_implicit_intents(in_file);
        }
        else if(in_file.exists()) {
            try {
                this.logWriter.write(LogType.OUT, true_filename + " Exists!", false);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(in_file);
                Element root = doc.getDocumentElement();
                NodeList root_children = root.getChildNodes();
                for(int i = 0; i < root_children.getLength(); i++) {
                    // TODO: only handles first level children have to recurse to all children
                    Node root_child = root_children.item(i);
                    parse_node_element(root_child, filename);
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.err.println("AndroidXMLUtility: Parsing XML " + filename + " failed.");
                if(Constants.PRINT_ST) {
                    e.printStackTrace();
                }
            }

        }
        else {
            this.logWriter.write(LogType.OUT, true_filename + " does NOT exist!", false);
        }
    }

    /**
     * write the ui elements to the log file
     */
    public void write_elements() {
        for(AndroidUIElement androidUIElement : this.androidUIElements) {
            this.logWriter.write(LogType.OUT, androidUIElement.toString(), false);
        }
    }

    /**
     * get a list of the names of the XML files (excluding those in Constants)
     * @return the list of XML file names
     */
    public List<String> get_xml_names() {
        List <String> names = new ArrayList<>();
        File apkF = new File(this.apk_path);
        ZipFile archive;
        try {
            archive = new ZipFile(apkF);
            Enumeration entries = archive.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if (Constants.XML_FILENAME.matcher(entryName).find() &&
                        !Constants.XML_EXCLUDES.contains(entryName)) {
                    names.add(entryName);
                }
            }
        } catch (IOException e) {
            System.err.println("AndroidXMLUtility: Getting XML names failed.");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
        return names;
    }

    /**
     * get the AndroidUIElements from the XML parse
     * @return the AndroidUIElements
     */
    public List<AndroidUIElement> get_android_UIElements() {
        return androidUIElements;
    }

    /**
     * get the implicit intents map
     * @return the implicit intents map
     */
    public Map<String, String> get_implicit_intents() {
        return this.implicit_intents;
    }
}
