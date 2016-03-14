package br.pucrio.inf.les.law.util;

import br.pucrio.inf.les.law.XMLawTestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigTest extends XMLawTestCase{
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ConfigTest.class);
    
    public void testFindFile(){
        LOG.info(Config.getInstance().getFileName());
        assertNotNull(Config.getInstance().getFileName());
        
    }
    
    public void testMediatorName(){
        assertNotNull(Constants.MEDIATOR_NAME.getValue());
    }
    
    public void testMediatorCommunicationClass(){
        assertNotNull(Constants.MEDIATOR_COMMUNICATION_CLASS.getValue());
    }
    
    public void testMediatorId(){
        assertNotNull(Constants.MEDIATOR_ID.getValue());
    }
    
}
