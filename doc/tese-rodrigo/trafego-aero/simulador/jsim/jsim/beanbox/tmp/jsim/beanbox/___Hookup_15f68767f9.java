// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import BankBean;
import jsim.process.SimulateListener;
import jsim.process.SimulateEvent;

public class ___Hookup_15f68767f9 implements jsim.process.SimulateListener, java.io.Serializable {

    public void setTarget(BankBean t) {
        target = t;
    }

    public void handleSimulate(jsim.process.SimulateEvent arg0) {
        target.startSim();
    }

    private BankBean target;
}
