import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by fire on 3/7/17.
 */
public class AndroidXMLUtility {
    private String apk_path;
    private LogWriter logWriter;

    /**
     * get the AndroidUIElements from the XML parse
     * @return the AndroidUIElements
     */
    public List<AndroidUIElement> getAndroidUIElements() {
        return androidUIElements;
    }

    private List<AndroidUIElement> androidUIElements;
    /**
     * Constructor for the AndroidXMLUtility
     * @param apk the file path for the APK
     */
    public AndroidXMLUtility(String apk) {
        this.apk_path = apk;
        this.androidUIElements = new ArrayList<>();
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
     * parse a single element
     * @param node the element
     * @param filename the filename that the element was found in
     */
    private void parse_node_element(Node node, String filename) {
        // TODO: find nested elements by using recursion and starting with root
        if(node.hasAttributes()) {
            String ui_type = node.getNodeName();
            this.logWriter.write(LogType.OUT, "---->" + ui_type,false);
            NamedNodeMap node_child_attrs = node.getAttributes();
            for(int j = 0; j < node_child_attrs.getLength(); j++) {
                Node child_attrs = node_child_attrs.item(j);
                if(child_attrs.getNodeName().equals(Constants.XML_ID_TAG)) {
                    this.logWriter.write(LogType.OUT, "---->" + child_attrs.getNodeValue(),false);
                    this.androidUIElements.add(new AndroidUIElement(child_attrs.getNodeValue(), ui_type, filename));
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
        if(in_file.exists()) {
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
}
