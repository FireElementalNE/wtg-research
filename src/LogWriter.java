import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogWriter {
    private File logFile;
    /**
     * format the message with a noparse tag and a timestamp
     * @param lt the LogType (enum)
     * @param msg the message
     * @return the msg with a timestamp and a noparse tag;
     */
    private String format_msg_noparse(LogType lt, String msg) {
        String prefix = this.get_prefix(lt);
        Date date = new Date();
        String format_string;
        format_string = "[%s][%s]: " + Constants.NOPARSE + " %s\n";
        return String.format(format_string, Constants.DATE_FORMAT.format(date), prefix, msg);
    }

    /**
     * format the msg normally (with no noparse tag) and a timestamp
     * @param lt the LogType (enum)
     * @param msg the message
     * @return the msg with a timestamp;
     */
    private String format_msg_parse(LogType lt, String msg) {
        String prefix = this.get_prefix(lt);
        Date date = new Date();
        String format_string;
        format_string = "[%s][%s]: %s\n";
        return String.format(format_string, Constants.DATE_FORMAT.format(date), prefix, msg);
    }

    /**
     * get the prefix string for a given LogType
     * @param lt the LogType (enum)
     * @return The prefix string
     */
    private String get_prefix(LogType lt) {
        switch (lt) {
            case ERR:
                return Constants.ERR_TAG;
            case OUT:
                return Constants.OUT_TAG;
            case SCR:
                return Constants.SCR_TAG;
            default:
                return "???";
        }
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
     * Write a message without a noparse tag
     * @param lt the LogType (enum)
     * @param str the message
     */
    public void write_parse(LogType lt, String str) {
        synchronized (this) {
            write(lt, str, true);
            notify();
        }
    }

    /**
     * Write a message with a noparse tag
     * @param lt the LogType (enum)
     * @param str the message
     */
    public void write_no_parse(LogType lt, String str) {
        synchronized (this) {
            write(lt, str, false);
            notify();
        }
    }

    /**
     * write to a log file
     * @param lt the log type (enum)
     * @param str the string to write
     * @param noparse whether to append Constants.NOPARSE
     *                to string (for gen_graph.py)
     */
    private void write(LogType lt, String str, boolean noparse) {
        try {
            FileWriter fw_out = new FileWriter(this.logFile.getAbsoluteFile(), true);
            BufferedWriter bw_out = new BufferedWriter(fw_out);
            if(noparse) {
                bw_out.write(format_msg_noparse(lt, str));
            }
            else {
                bw_out.write(format_msg_parse(lt, str));
            }
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
    }
}

