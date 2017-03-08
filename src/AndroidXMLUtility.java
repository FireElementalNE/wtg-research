import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by fire on 3/7/17.
 */
public class AndroidXMLUtility {
    private String apk_path;
    private LogWriter logWriter;

    /**
     * Constructor for the AndroidXMLUtility
     * @param apk the file path for the APK
     */
    public AndroidXMLUtility(String apk) {
        this.apk_path = apk;
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println(this.getClass().getSimpleName() + ": Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    /**
     * parse a given XML file in the apk
     * @param filename the filename (and path) of the XML file within the APK
     */
    public void parse_xml_file(String filename) {
        String true_filename = Constants.APK_UNPACK_PREFIX + filename;
        File in_file = new File(true_filename);
        if(in_file.exists()) {
            this.logWriter.write(LogType.OUT, true_filename + " Exists!", false);
        }
        else {
            this.logWriter.write(LogType.OUT, true_filename + " does NOT exist!", false);
        }
    }

    /**
     * get a list of the names of the XML files (excluding those in Constants)
     * @return the list of XML file names
     */
    public List<String> get_xml_names() {
        List <String> names = new ArrayList<>();
        // get AndroidManifest
        File apkF = new File(this.apk_path);
        ZipFile archive = null;
        try {
            archive = new ZipFile(apkF);
            Enumeration entries = archive.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if(Constants.XML_FILENAME.matcher(entryName).find() &&
                        !Constants.XML_EXCLUDES.contains(entryName)) {
                    names.add(entryName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error when looking for xml file names in apk: " + e);
        }
        return names;
    }
}
