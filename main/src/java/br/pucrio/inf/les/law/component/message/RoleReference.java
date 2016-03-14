package br.pucrio.inf.les.law.component.message;

import br.pucrio.inf.les.law.communication.AgentIdentification;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;

public class RoleReference {

    private RoleDescriptor roleDescriptor;
    private String roleInstance;
    private int multiplicity;
    private AgentIdentification roleInstanceValue;
    
    public void setRoleInstance(String roleInstance) {
        this.roleInstance = roleInstance;
    }
    public void setRoleDescriptor(RoleDescriptor roleRef) {
        this.roleDescriptor = roleRef;
    }
    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }
    public int getMultiplicity() {
        return multiplicity;
    }
    public RoleDescriptor getRoleDescriptor() {
        return roleDescriptor;
    }
    public String getRoleInstance() {
        return roleInstance;
    }
    
    public void setRoleInstanceValue(AgentIdentification roleInstanceValue){
    	this.roleInstanceValue = roleInstanceValue;
    }
    
    public AgentIdentification getRoleInstanceValue(){
    	return this.roleInstanceValue;
    }
    
    public String toString(){
        return "["+roleDescriptor+"], "+roleInstance+"= "+roleInstanceValue+", multi="+multiplicity;
    }
    
    
}
