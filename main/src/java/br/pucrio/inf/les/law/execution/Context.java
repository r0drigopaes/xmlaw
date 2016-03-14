package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Context {

	/**
	 * Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(Context.class);

	private Context parent = null;

	private ExecutionManager executionManager = null;

	private List<Context> children = new ArrayList<Context>();

	private Subject subject = null;

	public Context(ExecutionManager executionManager, Context parent) {
		this.parent = parent;
		if (parent != null) {
			this.parent.addChild(this);
		}
		this.executionManager = executionManager;
	}

	private void addChild(Context context) {
		if (!this.children.contains(context)) {
			this.children.add(context);
		}
	}

	public Context getParent() {
		return this.parent;
	}

	public List<IExecution> getExecutionsByDescriptorId(Id id) {
		return this.executionManager.getExecutionsByDescriptorIdAndContext(id,
				this);
	}

	public IDescriptor getDescriptorByDescriptorId(Id descriptorId) {
		return executionManager.getDescriptorByDescriptorId(descriptorId);
	}

	public void attachObserver(IObserver observer, int mask) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Attaching observer: " + observer + ". Mask: " + mask
					+ ". Context: " + this);
		}
		Subject subject = getSubject();
		subject.attachObserver(observer, mask);
	}

	public void detachObserver(IObserver observer, int mask) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Detaching observer: " + observer + ". Mask: " + mask
					+ ". Context: " + this);
		}
		Subject subject = getSubject();
		subject.detachObserver(observer, mask);
	}

	// public void detachAllObservers(int mask){
	// Subject subject = getSubject();
	// subject.detachAllObservers(mask);
	// }

	public void fireEventInContext(Event event) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Generating event: " + event + "["
					+ event.getEventGeneratorId() + "]" + " in context: "
					+ this);
		}
		LOG.info("Generating event: " + event + "["
				+ event.getEventGeneratorId() + "]" + " in context: " + this);
		Subject subject = getSubject();
		subject.fireEvent(event);
	}

	public void fire(Event event) {
		// propagates the event up through the tree
		fireUp(event);

		// propagates the event to all childdren
		fireDown(event);

		// Fire the event in this context
		fireEventInContext(event);

	}

	private void fireUp(Event event) {
		if (parent != null) {
			parent.fireUp(event);
			parent.fireEventInContext(event);
		}
	}

	private void fireDown(Event event) {
		for (Context child : children) {
			child.fireDown(event);
			child.fireEventInContext(event);
		}
	}

	public String toString() {
		return String.valueOf(hashCode());
	}

	/**
	 * It asks the execution manager for creating a new instance in this
	 * context.
	 * 
	 * @param descriptorId
	 * @param parameters
	 * @return
	 * @throws LawException
	 */
	public IExecution newInstance(Id descriptorId,
			Map<String, Object> parameters) throws LawException {
		return executionManager.newInstanceInContext(this, descriptorId,
				parameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.pucrio.inf.les.law.execution.ExecutionManager#enableTrigger(br.pucrio.inf.les.law.execution.IDescriptor,
	 *      br.pucrio.inf.les.law.execution.ITrigger,
	 *      br.pucrio.inf.les.law.execution.Context)
	 */
	public void enableTrigger(IDescriptor descriptor) {
		executionManager.enableTrigger(descriptor, this);
		// if (parent != null) {
		// // Listen for events generated at the parent
		// executionManager.enableTrigger(descriptor, parent);
		// }
	}

	public Subject getSubject() {
		if (this.subject == null) {
			this.subject = new Subject();
		}
		return this.subject;
	}

	public ExecutionManager getExecutionManager() {
		return executionManager;
	}

	public void stop() {
		if (parent != null) {
			parent.removeChild(this);
		}
	}

	public List<Context> getChildren() {
		return children;
	}

	private void removeChild(Context context) {
		children.remove(context);
	}

	public void stopExecution(IExecution execution) {
		executionManager.stopExecution(execution);

	}

}
