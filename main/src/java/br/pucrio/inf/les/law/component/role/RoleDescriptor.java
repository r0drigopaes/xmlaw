package br.pucrio.inf.les.law.component.role;

import br.pucrio.inf.les.law.model.Id;

public class RoleDescriptor {
    private Id id;

    public RoleDescriptor(Id id) {
        this.id = id;
    }

    public Id getId() {
        return id;
    }
    
    public String toString(){
        return id.toString();
    }
    
    
    public boolean equals(Object obj){
        if (! (obj instanceof RoleDescriptor) ){
            return false;
        }
        RoleDescriptor theObj = (RoleDescriptor)obj;
        return theObj.id.equals(id);
    }
    
    @Override
    public int hashCode() {    
    	return id.hashCode();
    }
}
