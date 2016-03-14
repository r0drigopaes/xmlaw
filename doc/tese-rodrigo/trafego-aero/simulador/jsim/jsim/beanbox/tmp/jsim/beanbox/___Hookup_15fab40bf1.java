// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import PhoneBean;
import jsim.process.SimulateListener;
import jsim.process.SimulateEvent;

public class ___Hookup_15fab40bf1 implements jsim.process.SimulateListener, java.io.Serializable {

    public void setTarget(PhoneBean t) {
        target = t;
    }

    public void handleSimulate(jsim.process.SimulateEvent arg0) {
        target.handleSimulate(arg0);
    }

    private PhoneBean target;
}
