package br.pucrio.inf.les.law.component.clock;

import java.util.Map;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.AbstractExecution;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rodrigopaes.utils.concurrency.Timer;

public class ClockExecution extends AbstractExecution implements Runnable {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ClockExecution.class);

    private Thread myThread;

    private long timeout;

    private ExecutionState executionState = ExecutionState.RUNNING ;

    private Timer timer;
    
    private Map<String, Object> parameters;

    public enum Type {
        ONCE, PERIODIC
    };

    public ClockExecution(Context context, long timeout, Type type,
            IDescriptor descriptor, Map<String, Object> parameters) {
        super(context, descriptor);
        this.context = context;
        this.timeout = timeout;
        Timer.Type timerType = null;
        executionState = ExecutionState.RUNNING;
        this.parameters = parameters;

        switch (type) {
        case ONCE:
            timerType = Timer.Type.ONCE;
            break;
        case PERIODIC:
            timerType = Timer.Type.PERIODIC;
            break;
        }
        timer = new Timer(timeout, timerType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating Clock execution ");
        }
        // starts the thread
        start();
    }

    public String toString() {
        return "Clock [" + getId() + "]";
    }

    public void start() {
        if (myThread == null) {
            myThread = new Thread(this, "Clock");
        }
        executionState = ExecutionState.RUNNING;
        myThread.start();
    }

    public void run() {
    	int eventTypeDeactivation = Masks.CLOCK_DEACTIVATION;
        timer.start();
        while (executionState.equals(ExecutionState.RUNNING)) {
            timer.tick();
            // Sai desse tick sob duas condições:
            // 1- clock tick
            // 2- Alguem chamou o stopExecution do ClockExecution, que por sua vez chamou o tick.stop();
            
            // É timeout quando: timer.hasFinished==true && timer.wasTimeout()  
            
            
            // Verifies if it is still running
            if (executionState.equals(ExecutionState.RUNNING)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(toString() + ": tick");
                }
                // Fires the event
                Event event = new Event(descriptor.getId(), Masks.CLOCKTICK, descriptor.getId());
                event.setContent(this.parameters);
                context.fire(event);
            }
            if (timer.hasFinished()) {
            	LOG.info("No more ticks to generate");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No more ticks to generate");
                }
                stopExecution();
                if (timer.wasTimeout()){
                	eventTypeDeactivation = Masks.CLOCK_TIMEOUT;
                }
            }
        } // End While
        if (LOG.isDebugEnabled()) {
            LOG.debug("Clock execution finished: " + toString());
        }
        Event event = new Event(descriptor.getId(), eventTypeDeactivation, descriptor.getId());
        event.setContent(this.parameters);
        context.fire(event);
        
    }

    public void stopExecution() {
        if (LOG.isDebugEnabled()) {
            LOG.debug(toString() + ": Stop execution of clock was called");
        }
        executionState = ExecutionState.FINISHED;
        timer.stop();
        myThread = null;
    }
    
    public boolean shouldStop(Event event) {
        return true;
    }

    public ExecutionState getExecutionState() {
        return executionState;
    }

	public void setExecutionState(ExecutionState state) {
		executionState = state;
	}
	
	public static void main(String[] args) {
		Timer mgatti = new Timer(86400000,Timer.Type.PERIODIC);
		System.out.println("Sim");
	}

}
