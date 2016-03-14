// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import BankBean;
import jsim.process.SimulateListener;
import jsim.process.SimulateEvent;

public class ___Hookup_15fb1d086f implements jsim.process.SimulateListener, java.io.Serializable {

    public void setTarget(BankBean t) {
        target = t;
    }

    public void handleSimulate(jsim.process.SimulateEvent arg0) {
        target.handleSimulate(arg0);
    }

    private BankBean target;
}
