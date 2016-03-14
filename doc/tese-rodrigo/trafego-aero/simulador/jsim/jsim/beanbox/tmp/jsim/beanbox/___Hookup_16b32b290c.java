// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import BankBean;
import jsim.jmessage.JsimListener;
import jsim.jmessage.JsimEvent;

public class ___Hookup_16b32b290c implements jsim.jmessage.JsimListener, java.io.Serializable {

    public void setTarget(BankBean t) {
        target = t;
    }

    public void notify(jsim.jmessage.JsimEvent arg0) {
        target.notify(arg0);
    }

    private BankBean target;
}
