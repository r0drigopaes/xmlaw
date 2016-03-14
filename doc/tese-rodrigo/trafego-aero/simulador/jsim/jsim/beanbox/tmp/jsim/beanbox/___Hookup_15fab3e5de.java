// Automatically generated event hookup file.

package tmp.jsim.beanbox;
import FoodCourtBean;
import jsim.process.SimulateListener;
import jsim.process.SimulateEvent;

public class ___Hookup_15fab3e5de implements jsim.process.SimulateListener, java.io.Serializable {

    public void setTarget(FoodCourtBean t) {
        target = t;
    }

    public void handleSimulate(jsim.process.SimulateEvent arg0) {
        target.handleSimulate(arg0);
    }

    private FoodCourtBean target;
}
