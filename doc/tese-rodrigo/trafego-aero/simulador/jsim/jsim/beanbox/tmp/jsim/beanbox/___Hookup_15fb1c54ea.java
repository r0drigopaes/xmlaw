// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import jsim.jrun.BatchMeansAgent;
import jsim.process.ReportListener;
import jsim.process.ReportEvent;

public class ___Hookup_15fb1c54ea implements jsim.process.ReportListener, java.io.Serializable {

    public void setTarget(jsim.jrun.BatchMeansAgent t) {
        target = t;
    }

    public void handleReport(jsim.process.ReportEvent arg0) {
        target.handleReport(arg0);
    }

    private jsim.jrun.BatchMeansAgent target;
}
