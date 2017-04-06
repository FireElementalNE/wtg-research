import soot.SootClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fire on 4/6/17.
 */
public class WTGGraphEdge {
    private SootClass source_class;



    List<String> targets;

    /**
     * constructor
     * @param source_class the class
     */
    WTGGraphEdge(SootClass source_class) {
        this.source_class = source_class;
        this.targets = new ArrayList<>();
    }

    /**
     * adds a target to the targets list iff it is not already in the list
     * @param target the target
     */
    public void add_target(String target) {
        if(!this.targets.contains(target)) {
            this.targets.add(target);
        }
    }

    /**
     * get the source name
     * @return the source name
     */
    public String get_name() {
        return this.source_class.getName();
    }

    /**
     * get the source class
     * @return the source class
     */
    public SootClass get_class() {
        return this.source_class;
    }

    /**
     * get the target list
     * @return the target list
     */
    public List<String> get_targets() {
        return targets;
    }

}
