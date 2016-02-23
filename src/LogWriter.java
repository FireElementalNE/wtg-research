import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private File file_out;
    private File file_err;
    private FileWriter fw_out;
    private FileWriter fw_err;
    private BufferedWriter bw_out;
    private BufferedWriter bw_err;
    private boolean is_closed;
    private void check_exists(File file) throws IOException {
        if(!file.exists()) {
            file.createNewFile();
        }
        else {
            file.delete();
            file.createNewFile();
        }
    }
    public LogWriter() throws IOException {
        this.file_out = new File(Constants.OUTPUT_FILE);
        this.file_err = new File(Constants.ERROR_FILE);
        check_exists(this.file_out);
        check_exists(this.file_err);
        this.is_closed = false;
        this.fw_out = new FileWriter(this.file_out.getAbsoluteFile());
        this.fw_err = new FileWriter(this.file_err.getAbsoluteFile());
        this.bw_out = new BufferedWriter(this.fw_out);
        this.bw_err = new BufferedWriter(this.fw_err);
    }
    public void write_stackdump(Exception e) {
        this.write_err(e.getMessage());
    }
    public void write_out(String str)  {
        if(!this.is_closed) {
            try {
                this.bw_out.write(str + "\n");
                this.bw_out.flush();
                if(Constants.DEBUG) {
                    System.out.println("LogWriter: " + str);
                }
            } catch (IOException e) {
                System.err.println("LogWriter: write_out Failed.");
                if(Constants.PRINT_ST) {
                    e.printStackTrace();
                }
                this.write_stackdump(e);
            }
        }
        else {
            System.err.println("Sorry I am closed.");
        }
    }
    public void write_err(String str) {
        if(!this.is_closed) {
            FileWriter fw = null;
            try {
                this.bw_err.write(str + "\n");
                this.bw_err.flush();
                if(Constants.DEBUG) {
                    System.out.println("LogWriter: " + str);
                }
            } catch (IOException e) {
                System.err.println("LogWriter: write_out Failed.");
                if(Constants.PRINT_ST) {
                    e.printStackTrace();
                }
                this.write_stackdump(e);
            }
        }
        else {
            System.err.println("Sorry I am closed.");
        }
    }
    public void close() {
        this.is_closed = true;
        try {
            this.bw_out.close();
            this.bw_err.close();
            this.fw_out.close();
            this.fw_err.close();
        } catch (IOException e) {
            System.err.println("LogWriter: close Failed.");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
            this.write_stackdump(e);
        }

    }
}

