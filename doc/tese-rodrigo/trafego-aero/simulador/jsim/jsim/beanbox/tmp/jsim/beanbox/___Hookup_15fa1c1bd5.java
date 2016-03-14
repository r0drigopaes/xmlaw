// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import jsim.jrun.ReplicationAgent;
import jsim.process.InformListener;
import jsim.process.InformEvent;

public class ___Hookup_15fa1c1bd5 implements jsim.process.InformListener, java.io.Serializable {

    public void setTarget(jsim.jrun.ReplicationAgent t) {
        target = t;
    }

    public void handleInform(jsim.process.InformEvent arg0) {
        target.handleInform(arg0);
    }

    private jsim.jrun.ReplicationAgent target;
}
