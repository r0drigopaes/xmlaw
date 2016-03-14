package br.pucrio.inf.les.law.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

import br.pucrio.inf.les.law.model.Id;


/**
 * Implementação básica da interface IEvent.
 * @author rbp & guga - LES (PUC-Rio)
 * @version $Revision$
 */
public class Event{
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(Event.class);

	public static final String MESSAGE = "message";
	public static final String CLOCK = "clock";
	public static final String NORM = "norm";
	public static final String ACTION = "action";
	public static final String ROLEREFERENCE = "roleReference";
	
	private Map<String,Object> content;
	
	/**
     * Id that identifies this event. 
	 */
    private Id eventId;
    
    /**
     * The Id of the generator of this event 
     */
    private Id eventGeneratorId;
    
    /**
     * Type of this event
     */
	protected int type;
	
	/**
     * 
     * @param eventType the Event Type. @see Masks
     * @param eventGeneratorId the Id of the genarator of this event.
	 */
    public Event(int eventType, Id eventGeneratorId){
        this.type = eventType;		
        this.eventGeneratorId = eventGeneratorId; 
        this.eventId = new Id();
        content = new HashMap<String,Object>();
	}
    
    /**
     * @param id the Event Id.
     * @param eventType the Event Type. @see Masks
     * @param eventGeneratorId the Id of the genarator of this event.
	 */
    public Event(Id id, int eventType, Id eventGeneratorId){
        this.type = eventType;		
        this.eventGeneratorId = eventGeneratorId; 
        this.eventId = id;
        content = new HashMap<String,Object>();
	}
    
    public void addEventContent(String eventContentStr, Object eventContent)
    {
    	content.put(eventContentStr, eventContent);
    }
    
    public Object getEventContent(String eventContentStr)
    {
    	return content.get(eventContentStr);
    }
    
    public Map<String,Object> getContent(){
    	return this.content;
    }
    
    
	
	public String toString(){
		return "Event["+Masks.getName(getType())+", id="+eventId+"]";
	}

	/* (non-Javadoc)
	 * @see br.pucrio.inf.les.law.shared.event.IEvent#getType()
	 */
	public int getType() {
		return type;
	}

    /**
     * @return Returns the eventGeneratorId.
     */
    public Id getEventGeneratorId() {
        return eventGeneratorId;
    }

    /**
     * @param eventGeneratorId The eventGeneratorId to set.
     */
    public void setEventGeneratorId(Id eventGeneratorId) {
        this.eventGeneratorId = eventGeneratorId;
    }

	public Id getEventId() {
		return eventId;
	}

	public void setContent(Map<String, Object> content) {
		this.content.putAll(content);
	}
}


/* ****************************************************************
 * UPDATE LOG:
 * 
 * $Log$
 * Revision 1.10  2006/05/04 20:48:44  mgatti
 * 04/05/2006: Alteração da implementação de Actions
 *
 * Revision 1.9  2006/05/02 23:33:55  mgatti
 * 02/05/2006:  XMLaw - Actions e Constraints
 * Está dando quando as duas cenas de uma mesma organização estão especificadas (o "saco" dos states e transitions está pegando tudo)
 *
 * Revision 1.8  2006/04/25 16:36:23  mgatti
 * 26/04/2006: Versão com alterações da implementação do broadcast.
 *
 * Revision 1.7  2006/02/22 20:49:30  mgatti
 * Maíra: 1ª Versão da Integração DimaX & XMLaw
 *
 * Revision 1.6  2006/02/01 16:46:13  mgatti
 * Maíra: Inclusão da Criticalidade - 01/02/2006
 *
 * Revision 1.5  2006/01/12 17:11:42  rbp
 * Colocados os casos de teste pra funcionar.
 *
 * Revision 1.4  2006/01/11 17:46:52  mgatti
 * Maíra: Inclusão da norma (Handler, Descritor e Execution)
 *
 * Revision 1.3  2006/01/11 16:05:01  lfrodrigues
 * Inclusao de Map para conteudo em eventos
 *
 * Revision 1.1  2005/12/05 18:29:48  rbp
 * Versão inicial
 *
 * Revision 1.1  2005/08/17 17:48:14  lfrodrigues
 * instalacao do maven como ferramenta de build automatico
 *
 * Revision 1.1  2005/05/10 16:22:57  guga
 * Inicio Refactoring
 *
 * Revision 1.4  2005/04/29 21:40:55  guga
 * Modificações na estrutura e primeira versao da implementacao do TAC SCM
 *
 * Revision 1.3  2004/11/24 01:06:04  rbp
 * Main modifications:
 * - Allow multiple instances of the same scene
 * - Introducing a 3 layer mapping from XML: XML --> Descriptor Layer --> Model Layer
 * - Framework of actions introduced; now it is possible use java code as consequence of some law configuration.
 * - Implementation of a case study in the context of product trading using SWT and JFace ( it requires to configure the eclipse for running the application)
 * - Some bugs were fixed altough I don't remember of them.
 *
 * Revision 1.1  2004/09/22 01:04:38  rbp
 * This version contains severals modifications:
 * - Package renaming
 * - Specification of laws from XML language
 * - Added a Sax based Xml parser for reading from XML
 * - Some hard-coded code were removed and placed in the Config.properties file
 * - Added the simple application example
 *
 * Revision 1.5  2004/09/10 17:49:44  rbp
 * Main modifications:
 * Message is not an event anymore.
 * The information can be carried through the InfoCarrier object.
 * TriggerManager complete modification. Now it allows multiple norm and clock instances.
 *
 * Revision 1.4  2004/07/14 21:08:16  rbp
 * Inserindo alterações para permitir multiplas organizações, cenários, não-determinismo etc.
 *
 * Revision 1.3  2004/07/06 16:53:29  guga
 * atualizacao subjects e observer por identificador
 *
 * Revision 1.2  2004/06/30 19:44:20  guga
 * Atualização execução paralela
 *
 * Revision 1.1  2004/06/29 02:30:02  rbp
 * Melhoramentos na interface gráfica: criação da barra de delay, arco no estado final, problema do circulo que estava sendo desenhando com buracos etc.
 * Mudanças na documentação.
 *
 * ***************************************************************/