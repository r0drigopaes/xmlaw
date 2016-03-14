package br.pucrio.inf.les.law.component.protocol;

import br.pucrio.inf.les.law.event.Masks;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

public class TransitionHandler extends ComposedHandler {

    public TransitionHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    /**
     * Exemplo:<br>
     * <Transition id="t1" from="s0" to="s1" ref="rfq" event-type="message_arrival"/> <br>
     * <Transition id="t1" from="s0" to="s1" ref="permissionToPay" event-type="norm_activation"/>
     */
    public void process(String namespaceURI, String localName, String qName, Attributes attr) throws LawException {
        String fromId       = attr.getValue("from");
        String toId         = attr.getValue("to");
        
        // Isto vai precisar de uma lista de pendências caso 
        // o elemento referenciado ainda não exista.
        String ref          = attr.getValue("ref");
        String eventType    = attr.getValue("event-type");

        State fromState = (State) handlerCache.get(new Id(fromId));
        State toState = (State) handlerCache.get(new Id(toId));        
        
        ProtocolDescriptor protocolDescriptor = (ProtocolDescriptor)stack.peek();
        
        Transition transitionDescriptor = new Transition( new Id(id),fromState,toState, protocolDescriptor ); 
        transitionDescriptor.addActivationCondition(new Id(ref),Masks.getMask(eventType));
        
        stack.push(transitionDescriptor);

                
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub
        
    }

}
