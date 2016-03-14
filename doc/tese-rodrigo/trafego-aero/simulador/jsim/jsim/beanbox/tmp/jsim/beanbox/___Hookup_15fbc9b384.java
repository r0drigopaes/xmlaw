// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import ERoomBean;
import jsim.process.SimulateListener;
import jsim.process.SimulateEvent;

public class ___Hookup_15fbc9b384 implements jsim.process.SimulateListener, java.io.Serializable {

    public void setTarget(ERoomBean t) {
        target = t;
    }

    public void handleSimulate(jsim.process.SimulateEvent arg0) {
        target.handleSimulate(arg0);
    }

    private ERoomBean target;
}
