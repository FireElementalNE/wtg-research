import soot.SootMethod;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;

import java.io.IOException;

/**
 * Created by fire on 4/9/17.
 */
public class GraphInferenceVisitor  extends AbstractStmtSwitch {

    private LogWriter logWriter;

    GraphInferenceVisitor() {
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("WTGGraph: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }

    /**
     * statement to catch assignment statements from AbstractStmtSwitch
     * @param stmt the current assignment statement
     */
    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        if(stmt.containsInvokeExpr()) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            SootMethod sootMethod = invokeExpr.getMethod();
            // find all of the calls to findViewById() that get assigned to something
            if(sootMethod.getName().equals(Constants.FIND_VIEW_BY_ID_METHOD)) {
                String left_type = stmt.getLeftOp().getType().toString();
                String right_type = stmt.getRightOp().getType().toString();
                String left_value = stmt.getLeftOpBox().getValue().toString();
                String right_value = stmt.getRightOpBox().getValue().toString();
                String msg = String.format("Assignment -> %s, %s  %s, %s", left_type, right_type, left_value, right_value);
                this.logWriter.write(LogType.OUT, msg, true);
            }
        }
    }
}
