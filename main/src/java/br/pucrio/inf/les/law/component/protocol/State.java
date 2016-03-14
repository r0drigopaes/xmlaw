package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.event.Event;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class State {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(State.class);

    public enum Type {
        INITIAL, EXECUTION, SUCCESS, FAILURE
    }

    private Id id;

    private Type type;

    private String label;

    /**
     * Transições que se originam neste estado. Ou seja, são transições de saída
     * deste estado.
     * 
     * @associates <{br.pucrio.inf.les.law.protocol.Transition}>
     * @directed true
     * @label has
     * @supplierCardinality 0..*
     */
    private ArrayList<Transition> transitions = new ArrayList<Transition>();

    public State(Id id, String label, Type type) {
        this.id = id;
        this.type = type;
        this.label = label;
    }

    // public static Type getType(String typeLabel) throws LawException {
    // if ("initial".equals(typeLabel)) {
    // return Type.INITIAL;
    // } else if ("success".equals(typeLabel)) {
    // return Type.SUCCESS;
    // } else if ("execution".equals(typeLabel)) {
    // return Type.EXECUTION;
    // } else if ("failure".equals(typeLabel)) {
    // return Type.FAILURE;
    // } else {
    // throw new LawException("Invalid state type: [" + typeLabel
    // + "]. Use: initial, success, execution or failure",
    // LawException.INVALID_STATE_TYPE);
    // }
    // }

    /*
     * Verifies the state
     */
    public boolean isInitial() {
        return this.type.equals(Type.INITIAL);
    }

    public boolean isSuccess() {
        return this.type.equals(Type.SUCCESS);
    }

    public boolean isFailure() {
        return this.type.equals(Type.FAILURE);
    }

    public boolean isExecution() {
        return this.type.equals(Type.EXECUTION);
    }

    /**
     * Adiciona uma transição que tem como origem este estado e como destino o
     * estado especificado na própria transição.
     * 
     * @param transition
     *            a transição de saída a ser adicionada a este estado.
     * @author rbp
     */
    public void addOutgoingTransition(Transition transition)
            throws LawException {
        if (this.type == Type.FAILURE || this.type == Type.SUCCESS) {
            throw new LawException(
                    "It is not allowed to connect a final state to any other state",
                    LawException.INVALID_PROTOCOL_SPECIFICATION);
        }
        // Verifica se a transição já não foi adicionada antes.
        if (!transitions.contains(transition)) {
        	LOG.debug("Adding transition " + transition.getId() + " to the state " + this.getId());
            transitions.add(transition);
        } else {
            LOG
                    .warn("Attempt to add a transition that was already added. State:["
                            + toString() + "], Transition[" + transition + "]");
        }
    }

    public String toString() {
        return this.label;
    }

    /**
     * Verifica se a partir deste estado existe alguma transição que é disparada
     * através do evento passado no parâmetro.
     * 
     * @param event
     *            o evento que talvez dispare alguma das transições de saída
     *            deste estado.
     * @return uma lista de próximos estados se alguma transição foi disparada.
     *         A lista será vazia caso nenhuma transição tenha sido ativada.
     * @throws LawException
     * @author rbp
     */
    public List<State> step(Event event, Context context) throws LawException {
        ArrayList<State> nextStates = new ArrayList<State>();
        LOG.debug("State["+this.getId()+"], number of outgoing transition:["+transitions.size()+"]");
        for (Transition transition : transitions) {
            
        	State to = transition.fire(event, context);
        	if (to != null){
                nextStates.add(to);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Transition [" + transition.getId()
                            + "] activated! (" + this.toString()
                            + ") --> (" + to.toString() + ")");
                }
            }
            	
        }
        return nextStates;
    }

    public Id getId() {
        return id;
    }

    public Transition getOutgoingTransition(Id transitionId) {
        for (Transition transition : transitions) {
            if (transition.getId().equals(transitionId)){
                return transition;
            }
        }
        return null;
    }
    
    
    public boolean equals(Object obj) 
    {
    	if(!(obj instanceof State))
    	{
    		return false;
    	}

    	State state = (State)obj;
    	return state.id.equals(id) && state.type.equals(type) && state.label.equals(label);
    }
    
    @Override
    public int hashCode() 
    {    
    	return (id.toString() + type.toString() + label).hashCode();
    }
    
}
