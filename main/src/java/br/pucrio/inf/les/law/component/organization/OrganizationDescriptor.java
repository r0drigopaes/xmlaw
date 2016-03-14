package br.pucrio.inf.les.law.component.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.pucrio.inf.les.law.component.ElementDescriptor;
import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisExecution;
import br.pucrio.inf.les.law.component.role.RoleDescriptor;
import br.pucrio.inf.les.law.component.scene.SceneDescriptor;
import br.pucrio.inf.les.law.execution.Context;
import br.pucrio.inf.les.law.execution.IExecution;
import br.pucrio.inf.les.law.execution.Trigger;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

public class OrganizationDescriptor extends ElementDescriptor {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory
            .getLog(OrganizationDescriptor.class);

    private String name;

    private List<SceneDescriptor> sceneDescriptors;
    
    private Map<Id,RoleDescriptor> roleDescriptors;


    public OrganizationDescriptor(Id descriptorId, String name) {
        super(descriptorId);
        this.name = name;
        sceneDescriptors = new ArrayList<SceneDescriptor>();
        roleDescriptors = new HashMap<Id,RoleDescriptor>();
    }

    public boolean needContext() {
        return true;
    }

    public IExecution createExecution(Context context,
            Map<String, Object> parameters) throws LawException {

    	CriticalityAnalysisExecution criticalityExecution = null;
        if (getCriticalityAnalysisDescriptor() != null){
        	criticalityExecution = 
        		(CriticalityAnalysisExecution) context
        			.newInstance(getCriticalityAnalysisDescriptor().getId(), null);
        }
        
        enableTriggers(context);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating organization execution");
        }
        return new OrganizationExecution(context, criticalityExecution, this);
    }

    public Trigger getTrigger() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        return name;
    }

    public void addSceneDescriptor(SceneDescriptor sceneDescriptor) {
        this.sceneDescriptors.add(sceneDescriptor);
    }

    public List<SceneDescriptor> getSceneDescriptors() {
        return sceneDescriptors;
    }
    
    public void addRoleDescriptors(RoleDescriptor roleDescriptor)
    {
    	this.roleDescriptors.put(roleDescriptor.getId(),roleDescriptor);
    }
    
    public Map<Id,RoleDescriptor> getRoleDescriptors()
    {
    	return this.roleDescriptors;
    }
    
    public boolean hasRoleDescriptor(Id id)
    {
    	return getRoleDescriptors().containsKey(id);
    }

    public boolean shouldCreate(Context context, Map<String, Object> parameters)
            throws LawException {
        return true;
    }
}
