import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private File file_out;
    private File file_err;
    private File file_scratch;
    public LogWriter(String className) throws IOException {
        this.file_out = new File(className + Constants.LOG_OUT_SUFFIX);
        this.file_err = new File(className + Constants.LOG_ERR_SUFFIX);
        this.file_scratch = new File(className + Constants.LOG_SCRATCH_SUFFIX);
        this.file_out.createNewFile();
        this.file_err.createNewFile();
        this.file_scratch.createNewFile();
    }
    private void write_file(File file, String str) {
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

    public void write_out(String str)  {
        this.write_file(this.file_out, str);
    }
    public void write_err(String str) {
        this.write_file(this.file_err, str);
    }
    public void write_scratch(String str) {
        this.write_file(this.file_scratch, str);
    }
}

