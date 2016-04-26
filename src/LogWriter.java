import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogWriter {
    private File fileOut, fileErr, fileScratch;

    /**
     * Add a timestamp to a msg
     * @param msg the msg
     * @return the msg with a timestamp;
     */
    public static String formatMsg(String msg) {
        Date date = new Date();
        return String.format("[%s]: %s\n", Constants.DATE_FORMAT.format(date),msg);
    }

    /**
     * Constructor for the log writting class, this class creates and writes to logs
     * @param className the current classname
     * @throws IOException
     */
    public LogWriter(String className) throws IOException {
        this.fileOut = new File(className + Constants.LOG_OUT_SUFFIX);
        this.fileErr = new File(className + Constants.LOG_ERR_SUFFIX);
        this.fileScratch = new File(className + Constants.LOG_SCRATCH_SUFFIX);
        this.fileOut.createNewFile();
        this.fileErr.createNewFile();
        this.fileScratch.createNewFile();
    }


    /**
     * Constructor for the log writting class, this class creates and writes to logs
     * @param className the current classname
     * @param passNum if InferenceVisitor puts the passnum in the name to
     *                differentiate between passes
     * @throws IOException
     */
    public LogWriter(String className, int passNum) throws IOException {
        this.fileOut = new File(className + Integer.toString(passNum) + Constants.LOG_OUT_SUFFIX);
        this.fileErr = new File(className + Integer.toString(passNum) + Constants.LOG_ERR_SUFFIX);
        this.fileScratch = new File(className + Integer.toString(passNum) + Constants.LOG_SCRATCH_SUFFIX);
        this.fileOut.createNewFile();
        this.fileErr.createNewFile();
        this.fileScratch.createNewFile();
    }

    /**
     * write to a log file
     * @param file the file to write to
     * @param str the string to write
     */
    private void writeFile(File file, String str) {
        synchronized (this) {
            try {
                FileWriter fw_out = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw_out = new BufferedWriter(fw_out);
                bw_out.write(formatMsg(str));
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

    /**
     * write to the stdout log
     * @param str the string to write
     */
    public void writeOut(String str)  {
        this.writeFile(this.fileOut, str);
    }

    /**
     * write to the stderr log
     * @param str the string to write
     */
    public void writeErr(String str) {
        this.writeFile(this.fileErr, str);
    }

    /**
     * write to the scratch log, this is for testing purposes
     * @param str the string to write
     */
    public void writeScratch(String str) {
        this.writeFile(this.fileScratch, str);
    }
}

