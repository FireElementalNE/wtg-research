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
        InputStream xml_input_stream = get_input_stream(filename);
        StringBuilder sb = new StringBuilder();
        if(xml_input_stream != null) {
                int i;
                char c;
            try {
                // TODO: Fix this, incorrect encoding (I am JUST trying to get it to print!!!!)
                while((i = xml_input_stream.read())!=-1) {
                    c = (char)i;
                    sb.append(c);
                }
                this.logWriter.write(LogType.OUT, sb.toString(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            this.logWriter.write(LogType.ERR, filename + ": failed to retreive input stream", false);
        }
    }

    /**
     * get an input stream from a file in the APK
     * @param filename the filename in the APK
     * @return an input stream to that file
     */
    private InputStream get_input_stream(String filename) {
        InputStream is = null;
        // get AndroidManifest
        File apkF = new File(this.apk_path);
        ZipFile archive = null;
        try {
            archive = new ZipFile(apkF);
            Enumeration entries = archive.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if(Objects.equals(filename, entryName)) {
                    is = archive.getInputStream(entry);
                    return is;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("Error when looking for xml file input stream  in apk: " + e);
        }
        return is;
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
