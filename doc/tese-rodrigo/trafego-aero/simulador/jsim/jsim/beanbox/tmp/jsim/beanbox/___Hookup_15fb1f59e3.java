// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import jsim.jrun.BatchMeansAgent;
import jsim.jrun.InstructListener;
import jsim.jrun.InstructEvent;

public class ___Hookup_15fb1f59e3 implements jsim.jrun.InstructListener, java.io.Serializable {

    public void setTarget(jsim.jrun.BatchMeansAgent t) {
        target = t;
    }

    public void handleInstruct(jsim.jrun.InstructEvent arg0) {
        target.handleInstruct(arg0);
    }

    private jsim.jrun.BatchMeansAgent target;
}
