import soot.*;
import soot.jimple.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
            SootClass method_class = method.getDeclaringClass();
            if(method.isConstructor()
                    && method.getParameterCount() == 2
                    && method.getParameterType(0).toString().equals(Constants.CONTEXT_CLASS)
                    && method.getParameterType(1).toString().contains(Constants.JAVA_CLASS_CLASS)
                    && method_class.getName().contains("Intent")) {
                this.logWriter.write_out("TYPE 1: " + method.getDeclaringClass());
                ValueBox valueBox = stmt.getInvokeExprBox();
                InvokeExpr invokeExpr = stmt.getInvokeExpr();
                this.logWriter.write_out("\tVALUE BOCX: " + valueBox.getValue().toString());
                this.logWriter.write_out("\t" + invokeExpr.getMethodRef().declaringClass());
                SootMethodRef sootMethodRef = invokeExpr.getMethodRef();
                this.logWriter.write_out("\t" + method.getSignature());
            }
            /*if(method.getName().equals("SetOnClickListner")) {
                    if(!method.isConstructor()
                            && method.getParameterCount() == 1) {
                            && method.getParameterType(0).toString().equals(Constants.ANON_VIEW_ONCLICK_LISTNER)) {
                    this.logWriter.write_out("TYPE 2: " + method.getDeclaringClass());
                    ValueBox valueBox = stmt.getInvokeExprBox();
                    this.logWriter.write_out("\tVALUE BOCX: " + valueBox.getValue().toString());
                }
            }*/
        }
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        /*Value left_op = stmt.getLeftOp();
        Value right_op = stmt.getRightOp();
        if(stmt.containsInvokeExpr()) {
            SootMethod method = stmt.getInvokeExpr().getMethod();
            SootClass method_class = method.getDeclaringClass();
            this.logWriter.write_out(method.toString());
        }*/
    }



}
