package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.Map;

public interface IDescriptor {

    /**
     * Return true if the execution created by this descriptor must run on its
     * own context. If the execution does not define a context, then this method
     * should return false.
     * 
     * @return
     */
    public boolean needContext();

    public boolean shouldCreate(Context context, Map<String,Object> parameters) throws LawException;
    
    /**
     * Creates an execution instance of this descriptor
     * 
     * @param context
     *            the context in which this execution will be running.
     * @param parameters
     *            some perameters needed to initialize the execution
     * @return the instance
     * @throws LawException
     */
    public IExecution createExecution(Context context,
            Map<String, Object> parameters) throws LawException;

    public Id getId();

    /**
     * If this descriptor may generate execution instances through triggers,
     * then this method should return a trigger that informs which are the
     * conditions for activation and deactivation.<br>
     * Case this descriptor does not need a trigger, then it just return null.
     * 
     * @return
     */
    public Trigger getTrigger();

}
