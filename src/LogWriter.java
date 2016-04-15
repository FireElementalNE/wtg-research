import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private File fileOut, fileErr, fileScratch;

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
     * write to a log file
     * @param file the file to write to
     * @param str the string to write
     */
    private void writeFile(File file, String str) {
        synchronized (this) {
            try {
                FileWriter fw_out = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw_out = new BufferedWriter(fw_out);
                bw_out.write(str + "\n");
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

