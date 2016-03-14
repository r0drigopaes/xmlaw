package br.pucrio.inf.les.law.component.scene;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;

import br.pucrio.inf.les.law.component.organization.OrganizationDescriptor;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.base.ComposedHandler;

public class SceneHandler extends ComposedHandler{

    public SceneHandler(Stack<Object> stack, DescriptorTable table, Hashtable<Id, Object> handlerCache) {
        super(stack, table, handlerCache);
    }

    @Override
    public void process(String namespaceURI, String localName, String qName, Attributes attr) throws LawException {
        
        
        String strTimeToLive = attr.getValue("time-to-live");
        long timeToLive;
        if ("infinity".equals(strTimeToLive)){
            timeToLive = -1;
        }else{
            if (strTimeToLive != null){
                timeToLive = Long.parseLong(strTimeToLive);
            }else{
                throw new LawException("Time to live in scene " + getId() + " is missing.",LawException.MISSING_TIME_TO_LIVE);
            }
            
        }
        
        SceneDescriptor sceneDescriptor = new SceneDescriptor(getId(),timeToLive);
        
        OrganizationDescriptor organizationDescriptor = (OrganizationDescriptor) stack.peek();
        organizationDescriptor.addSceneDescriptor(sceneDescriptor);
        sceneDescriptor.setOrganizationDescriptor(organizationDescriptor);
        
        stack.push(sceneDescriptor);
        table.add(sceneDescriptor);
        
    }

    @Override
    public void solvePendencies() throws LawException {
        // TODO Auto-generated method stub
        
    }
}
