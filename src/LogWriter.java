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
     * @return the msg with a timestamp;
     */
    private static String formatMsg(LogType lt, String msg) {
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
        return String.format("[%s][%s]: %s\n", Constants.DATE_FORMAT.format(date), prefix, msg);
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
     * Constructor for the log writting class, this class creates and writes to logs
     * @param className the current classname
     * @param passNum if InferenceVisitor puts the passnum in the name to
     *                differentiate between passes
     * @throws IOException
     */
    public LogWriter(String className, int passNum) throws IOException {
        this.logFile = new File(className + Integer.toString(passNum) + Constants.LOG_SUFFIX);
        this.logFile.createNewFile();
    }

    /**
     * write to a log file
     * @param lt the log type (enum)
     * @param str the string to write
     */
    public void write(LogType lt, String str) {
        synchronized (this) {
            try {
                FileWriter fw_out = new FileWriter(this.logFile.getAbsoluteFile(), true);
                BufferedWriter bw_out = new BufferedWriter(fw_out);
                bw_out.write(formatMsg(lt, str));
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

