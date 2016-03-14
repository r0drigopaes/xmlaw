// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import ERoomBean;
import jsim.process.InjectListener;
import jsim.process.InjectEvent;

public class ___Hookup_15fc1838e6 implements jsim.process.InjectListener, java.io.Serializable {

    public void setTarget(ERoomBean t) {
        target = t;
    }

    public void handleInject(jsim.process.InjectEvent arg0) {
        target.handleInject(arg0);
    }

    private ERoomBean target;
}
