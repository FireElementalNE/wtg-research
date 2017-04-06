import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogWriter {
    private File logFile;
    /**
     * Add a timestamp to a msg
     * @param lt the log type (enum)
     * @param msg the msg
     * @param noparse whether to append Constants.NOPARSE
     *                to string (for gen_graph.py)
     * @return the msg with a timestamp;
     */
    private static String format_msg(LogType lt, String msg, boolean noparse) {
        String prefix;
        switch (lt) {
            case ERR:
                prefix = Constants.ERR_TAG;
                break;
            case OUT:
                prefix = Constants.OUT_TAG;
                break;
            case SCR:
                prefix = Constants.SCR_TAG;
                break;
            default:
                prefix = "???";
                break;
        }
        Date date = new Date();
        String format_string;
        if(noparse) {
            format_string = "[%s][%s]: " + Constants.NOPARSE + " %s\n";
        }
        else {
            format_string = "[%s][%s]: %s\n";
        }
        return String.format(format_string, Constants.DATE_FORMAT.format(date), prefix, msg);
    }

    /**
     * Constructor for the log writting class, this class creates and writes to logs
     * @param className the current classname
     * @throws IOException
     */
    public LogWriter(String className) throws IOException {
        this.logFile = new File(className + Constants.LOG_SUFFIX);
        this.logFile.createNewFile();
    }

    /**
     * write to a log file
     * @param lt the log type (enum)
     * @param str the string to write
     * @param noparse whether to append Constants.NOPARSE
     *                to string (for gen_graph.py)
     */
    public void write(LogType lt, String str, boolean noparse) {
        synchronized (this) {
            try {
                FileWriter fw_out = new FileWriter(this.logFile.getAbsoluteFile(), true);
                BufferedWriter bw_out = new BufferedWriter(fw_out);
                bw_out.write(format_msg(lt, str, noparse));
                bw_out.flush();
                if(Constants.DEBUG) {
                    System.out.println("LogWriter: " + str);
                }
                bw_out.close();
                fw_out.close();
            } catch (IOException e) {
                System.err.println("LogWriter: write Failed.");
                if(Constants.PRINT_ST) {
                    e.printStackTrace();
                }
            }
            notify();
        }
    }
}

