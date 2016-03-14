// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import BankBean;
import jsim.process.InjectListener;
import jsim.process.InjectEvent;

public class ___Hookup_15fb2266bf implements jsim.process.InjectListener, java.io.Serializable {

    public void setTarget(BankBean t) {
        target = t;
    }

    public void handleInject(jsim.process.InjectEvent arg0) {
        target.handleInject(arg0);
    }

    private BankBean target;
}
