// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import jsim.beanbox.BeanBox;
import jsim.process.SimulateListener;
import jsim.process.SimulateEvent;

public class ___Hookup_15f688f508 implements jsim.process.SimulateListener, java.io.Serializable {

    public void setTarget(jsim.beanbox.BeanBox t) {
        target = t;
    }

    public void handleSimulate(jsim.process.SimulateEvent arg0) {
        target.equals(arg0);
    }

    private jsim.beanbox.BeanBox target;
}
