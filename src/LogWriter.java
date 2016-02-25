import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private File file_out;
    private File file_err;
    public LogWriter(String out_file, String err_file) throws IOException {
        this.file_out = new File(out_file);
        this.file_err = new File(err_file);
        this.file_out.createNewFile();
        this.file_err.createNewFile();
    }
    public void write_out(String str)  {
        synchronized (this) {
            try {
                FileWriter fw_out = new FileWriter(this.file_out.getAbsoluteFile(), true);
                BufferedWriter bw_out = new BufferedWriter(fw_out);
                bw_out.write(str + "\n");
                bw_out.flush();
                if(Constants.DEBUG) {
                    System.out.println("LogWriter: " + str);
                }
                bw_out.close();
                fw_out.close();
            } catch (IOException e) {
                System.err.println("LogWriter: write_out Failed.");
                if(Constants.PRINT_ST) {
                    e.printStackTrace();
                }
            }
            notify();
        }
    }
    public void write_err(String str) {
        synchronized (this) {
            try {
                FileWriter fw_err = new FileWriter(this.file_err.getAbsoluteFile(), true);
                BufferedWriter bw_err = new BufferedWriter(fw_err);
                bw_err.write(str + "\n");
                bw_err.flush();
                if (Constants.DEBUG) {
                    System.out.println("LogWriter: " + str);
                }
                bw_err.close();
                fw_err.close();
            } catch (IOException e) {
                System.err.println("LogWriter: write_out Failed.");
                if (Constants.PRINT_ST) {
                    e.printStackTrace();
                }
            }
            notify();
        }
    }
}

