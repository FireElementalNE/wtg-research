import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class InferenceVisitor extends AbstractStmtSwitch {
    public LogWriter logWriter;
    private InferenceTransformer t;
    public InferenceVisitor(InferenceTransformer inferenceTransformer) {
        this.t = inferenceTransformer;
        try {
            this.logWriter = new LogWriter(Constants.INF_VISITOR_OUTPUT_FILE,
                    Constants.INF_VISITOR_ERROR_FILE);
        } catch (IOException e) {
            System.err.println("InferenceVisitor: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        if(stmt.containsInvokeExpr()) {
            SootMethod method = stmt.getInvokeExpr().getMethod();
            List<Type> types = method.getParameterTypes();
            if(method.isConstructor()
                    && method.getParameterCount() == 2
                    && method.getParameterType(0).toString().equals(Constants.CONTEXT_CLASS)
                    && method.getParameterType(1).toString().contains(Constants.JAVA_CLASS_CLASS)
                    && method.getDeclaringClass().getName().contains("Intent")) {
                String result = "rawr";
                this.logWriter.write_out("TYPE 2: " + result);
            }
        }
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        Value left_op = stmt.getLeftOp();
        Value right_op = stmt.getRightOp();
        if(stmt.containsInvokeExpr()) {
            SootMethod method = stmt.getInvokeExpr().getMethod();
            if(method.isConstructor()) {
                this.logWriter.write_out("Intent: \n\t"
                        + left_op.getType().toString() + "\n\t"
                        + right_op.getType().toString() + "\n\t"
                        + method.getName());
            }
        }
    }

    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        // this.logWriter.write_out("Return Statment");
    }

    @Override
    public void caseIdentityStmt(IdentityStmt stmt) {
        // this.logWriter.write_out("Identity Statment");
    }

    @Override
    public void caseIfStmt(IfStmt stmt) {
        // this.logWriter.write_out("If Statement");
    }
}
