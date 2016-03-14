package br.pucrio.inf.les.law.component.norm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.message.RoleReference;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.event.IObserver;
import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.event.Subject;
import br.pucrio.inf.les.law.execution.ExecutionManager;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.XMLawParserScenario;

public class NormExecutionTest extends XMLawParserScenario implements IObserver {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(NormExecutionTest.class);

    private int counter = 0;

    private IExecution organization;

    private RoleReference roleReference;

    public void mount() throws LawException {
        ExecutionManager manager = new ExecutionManager(table);

        // Creates the organization instance
        this.organization = manager.newInstance(null, new Id("auction"), null);
        
        
        RoleDescriptor roleDescriptor = new RoleDescriptor(new Id("seller"));
        this.roleReference = new RoleReference();        
        roleReference.setRoleDescriptor(roleDescriptor);
        roleReference.setRoleInstance("$seller.instance");
        AgentIdentification agentId = new AgentIdentification("theSeller@agent");        
        roleReference.setRoleInstanceValue(agentId);
    }

    public void testNormEnableCondition() throws LawException {
        mount();

        // This event should activate the norms
        activateNorms();
        // There are 3 norms activated with the transition t1
        assertTrue(counter == 1);
    }

    /**
     * 
     */
    private void activateNorms() {
    	
        Event activation = new Event(Masks.TRANSITION_ACTIVATION,new Id("t1"));
        activation.addEventContent(Event.NORM,roleReference);
        organization.getContext().attachObserver(this, Masks.NORM_ACTIVATION);
        counter = 0;
        organization.getContext().fire(activation);
        organization.getContext().detachObserver(this, Masks.NORM_ACTIVATION);
    }

    public void testNormDisableCondition() throws LawException {
        mount();
        activateNorms();

        // This event should DEactivate the norms
        Event deactivation = new Event(Masks.SCENE_SUCCESSFUL_COMPLETION,new Id("negotiation"));
        deactivation.addEventContent(Event.NORM,roleReference);

        organization.getContext().attachObserver(this, Masks.NORM_DEACTIVATION);
        counter = 0;
        organization.getContext().fire(deactivation);
        // There is 1 norm activated with the transition t1 and with the rolereference used
        assertTrue(counter == 1);

    }

    public void update(Event event) throws LawException {
        counter++;
    }

	public void notifySubjectDeath(Subject subject) {
		// TODO Auto-generated method stub
		
	}

}
