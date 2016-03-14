// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import jsim.jrun.ReplicationAgent;
import jsim.process.ReportListener;
import jsim.process.ReportEvent;

public class ___Hookup_15fab14cc0 implements jsim.process.ReportListener, java.io.Serializable {

    public void setTarget(jsim.jrun.ReplicationAgent t) {
        target = t;
    }

    public void handleReport(jsim.process.ReportEvent arg0) {
        target.handleReport(arg0);
    }

    private jsim.jrun.ReplicationAgent target;
}
