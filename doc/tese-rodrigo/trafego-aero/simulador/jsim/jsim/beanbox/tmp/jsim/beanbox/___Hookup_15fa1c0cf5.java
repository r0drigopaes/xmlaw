// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import ERoomBean;
import jsim.process.InformListener;
import jsim.process.InformEvent;

public class ___Hookup_15fa1c0cf5 implements jsim.process.InformListener, java.io.Serializable {

    public void setTarget(ERoomBean t) {
        target = t;
    }

    public void handleInform(jsim.process.InformEvent arg0) {
        target.fireInformEvent();
    }

    private ERoomBean target;
}
