import soot.SootClass;
import soot.util.Chain;

import java.util.regex.Matcher;

/**
 * Created by fire on 4/29/16.
 */
public class Utilities {
    /**
     * Checks if a class is anonymous
     * @param sootClass the class
     * @return true iff the class is anonymous
     */
    public static boolean isAnon(SootClass sootClass) {
        return sootClass.getName().contains("$");
    }

    /**
     * removes (BASIC) anon declaration from class
     * TODO: fix this to handle non basic anon declarations
     * @param sootClass the soot class
     * @return the name of the soot class with all '$' removed
     */
    public static String removeAnon(SootClass sootClass) {
        if(isAnon(sootClass)) {
            return sootClass.getName().replace("$", "");
        }
        else {
            return sootClass.getName();
        }
    }

    /**
     * Checks a class hierarchy to see if it is descended from android.app.Activity
     * does this recursively
     * @param sootClass the current class being checked
     * @param ancestor the superclass that we are checking
     * @return a boolean that is true iff the current class IS descended from the ancestor
     */
    static boolean checkAncestry(SootClass sootClass, String ancestor) {
        if(!sootClass.hasSuperclass()) {
            return false;
        }
        else if(sootClass.hasSuperclass()) {
            SootClass methodSuperclass = sootClass.getSuperclass();
            return methodSuperclass.getName().equals(ancestor) || checkAncestry(methodSuperclass, ancestor);
        }
        return false;
    }

    /**
     * checks the implemented interfaces of a class to see if one is interfaceTarget
     * removes anonymous declarations
     * @param sootClass the class
     * @param interfaceTarget the target interface
     * @return true iff the class implements the target interface
     */
    static boolean checkInterfaces(SootClass sootClass, String interfaceTarget) {
        Chain<SootClass> interfaces = sootClass.getInterfaces();
        for(SootClass inter : interfaces) {
            String className = removeAnon(inter);
            if(className.equals(interfaceTarget)) {
                return true;
            }
        }
        return false;
    }

    /**
     * tests whether a class is an android class (part of the android packager)
     * @param sootClass the class
     * @return true iff the class is an android class
     */
    static boolean androidSkip(SootClass sootClass) {
        Matcher matcher = Constants.ANDROID_SKIP.matcher(sootClass.getName());
        return matcher.find();
    }
}
