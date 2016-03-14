// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import jsim.jrun.BatchMeansAgent;
import jsim.process.InformListener;
import jsim.process.InformEvent;

public class ___Hookup_15fa23464b implements jsim.process.InformListener, java.io.Serializable {

    public void setTarget(jsim.jrun.BatchMeansAgent t) {
        target = t;
    }

    public void handleInform(jsim.process.InformEvent arg0) {
        target.handleInform(arg0);
    }

    private jsim.jrun.BatchMeansAgent target;
}
