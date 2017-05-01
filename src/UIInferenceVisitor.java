import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;

import java.io.IOException;

/**
 * Created by fire on 5/1/17.
 */
public class UIInferenceVisitor extends AbstractStmtSwitch {
    private LogWriter logWriter;

    /**
     * constructor
     */
    public UIInferenceVisitor() {
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("InferenceVisitor: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    /**
     * in case of assignment statment
     * @param stmt the assignement statment
     */
    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        Value left = stmt.getLeftOp();
        Value right = stmt.getRightOp();
        String msg = String.format("<%s> = <%s>", left.toString(), right.toString());
        this.logWriter.write(LogType.OUT, msg, true);
    }

}
