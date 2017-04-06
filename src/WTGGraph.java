import java.io.IOException;
import java.util.List;

/**
 * Created by fire on 4/6/17.
 */
public class WTGGraph {
    private List<WTGGraphNode> nodes;
    private List<WTGGraphEdge> edges;
    private LogWriter logWriter;
    /**
     * Constructor
     */
    WTGGraph(List <WTGGraphNode> nodes, List<WTGGraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        try {
            this.logWriter = new LogWriter(this.getClass().getSimpleName());
        } catch (IOException e) {
            System.err.println("WTGGraph: Declaring LogWriter Failed");
            if(Constants.PRINT_ST) {
                e.printStackTrace();
            }
        }
        this.logWriter.write(LogType.OUT, "works", false);
    }
}
