// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import jsim.jrun.BatchMeansAgent;
import jsim.jmessage.JsimListener;
import jsim.jmessage.JsimEvent;

public class ___Hookup_16b32b2cf5 implements jsim.jmessage.JsimListener, java.io.Serializable {

    public void setTarget(jsim.jrun.BatchMeansAgent t) {
        target = t;
    }

    public void notify(jsim.jmessage.JsimEvent arg0) {
        target.notify(arg0);
    }

    private jsim.jrun.BatchMeansAgent target;
}
