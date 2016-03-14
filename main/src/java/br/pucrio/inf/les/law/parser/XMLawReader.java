package br.pucrio.inf.les.law.parser;

import br.pucrio.inf.les.law.component.ActivationElementRefHandler;
import br.pucrio.inf.les.law.component.DeactivationElementRefHandler;
import br.pucrio.inf.les.law.component.action.ActionHandler;
import br.pucrio.inf.les.law.component.clock.ClockHandler;
import br.pucrio.inf.les.law.component.constraint.ConstraintHandler;
import br.pucrio.inf.les.law.component.message.ContentEntryHandler;
import br.pucrio.inf.les.law.component.message.MessageHandler;
import br.pucrio.inf.les.law.component.message.ReceiverHandler;
import br.pucrio.inf.les.law.component.message.SenderHandler;
import br.pucrio.inf.les.law.component.norm.NormAssigneeHandler;
import br.pucrio.inf.les.law.component.norm.NormHandler;
import br.pucrio.inf.les.law.component.norm.ActivatedNormRefHandler;
import br.pucrio.inf.les.law.component.norm.DeactivatedNormRefHandler;
import br.pucrio.inf.les.law.component.organization.OrganizationHandler;
import br.pucrio.inf.les.law.component.protocol.ProtocolHandler;
import br.pucrio.inf.les.law.component.protocol.StateHandler;
import br.pucrio.inf.les.law.component.protocol.TransitionHandler;
import br.pucrio.inf.les.law.component.role.RoleHandler;
import br.pucrio.inf.les.law.component.scene.CreatorSceneHandler;
import br.pucrio.inf.les.law.component.scene.EntranceSceneHandler;
import br.pucrio.inf.les.law.component.scene.EntranceSceneStateHandler;
import br.pucrio.inf.les.law.component.scene.SceneHandler;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.component.criticality.CriticalityAnalysisHandler;
import br.pucrio.inf.les.law.component.criticality.DecreaseHandler;
import br.pucrio.inf.les.law.component.criticality.EventAssigneeHandler;
import br.pucrio.inf.les.law.component.criticality.IncreaseHandler;
import br.pucrio.inf.les.law.component.criticality.WeightHandler;
import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;
import br.pucrio.inf.les.law.parser.handler.LawsHandler;
import br.pucrio.inf.les.law.parser.handler.base.AbstractHandler;
import br.pucrio.inf.les.law.parser.saxmapper.SaxMapper;
import br.pucrio.inf.les.law.parser.saxmapper.TagTracker;

import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Rodrigo - LES (PUC-Rio)
 * @version $Revision$
 */
public class XMLawReader extends SaxMapper {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(XMLawReader.class);

    private LawsHandler root = null;

    private DescriptorTable table = new DescriptorTable();

    private Stack<Object> stack = new Stack<Object>();

    private Hashtable<Id, Object> handlerCache = new Hashtable<Id, Object>();

    public XMLawReader(DescriptorTable table) {
        this.table = table;
        createTagStack();
    }

    public Object getMappedObject() throws LawException {
        root.solvePendencies();
        return table;
    }

    public TagTracker createTagTrackerNetwork() {
        root = new LawsHandler(stack, table, handlerCache);
        root.track("/Laws", root);
        
        AbstractHandler activationElement = new ActivationElementRefHandler(stack, table, handlerCache);
        AbstractHandler deactivationElement = new DeactivationElementRefHandler(stack, table, handlerCache);

         root.track("/Laws/LawOrganization",new OrganizationHandler(stack, table, handlerCache));
                  
         root.track("/Laws/LawOrganization/Clock",new ClockHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Clock/Activations/Element",activationElement);
         root.track("/Laws/LawOrganization/Clock/Deactivations/Element",deactivationElement);
         
         // Norms in the organization context
         root.track("/Laws/LawOrganization/Norm",new
                 NormHandler(stack, table, handlerCache));    
         
         root.track("/Laws/LawOrganization/Norm/Assignee",new
                 NormAssigneeHandler(stack, table, handlerCache));
         
         root.track("/Laws/LawOrganization/Norm/Activations/Element",activationElement);
         root.track("/Laws/LawOrganization/Norm/Deactivations/Element",deactivationElement);
             
         // END NORMS
         
         //CriticalityAnalysis in the organization context
         root.track("/Laws/LawOrganization/CriticalityAnalysis",new 
          		CriticalityAnalysisHandler(stack, table, handlerCache));
          root.track("/Laws/LawOrganization/CriticalityAnalysis/Weight",new 
  				WeightHandler(stack, table, handlerCache));
          root.track("/Laws/LawOrganization/CriticalityAnalysis/Increases/Increase",new 
  				IncreaseHandler(stack, table, handlerCache));
          
  		root.track("/Laws/LawOrganization/CriticalityAnalysis/Increases/Increase/Assignee",new 
  				EventAssigneeHandler(stack, table, handlerCache));
  		root.track("/Laws/LawOrganization/CriticalityAnalysis/Decreases/Decrease",new 
  				DecreaseHandler(stack, table, handlerCache));
  		root.track("/Laws/LawOrganization/CriticalityAnalysis/Decreases/Decrease/Assignee",new 
  				EventAssigneeHandler(stack, table, handlerCache));
  		//End CriticalityAnalysis

         root.track("/Laws/LawOrganization/Scene",new SceneHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/Creators/Creator",new CreatorSceneHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/Entrance/Participant",new EntranceSceneHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/Entrance/Participant/State",new EntranceSceneStateHandler(stack, table, handlerCache));
         
         root.track("/Laws/LawOrganization/Scene/CriticalityAnalysis",new 
         		CriticalityAnalysisHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/CriticalityAnalysis/Weight",new 
 				WeightHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/CriticalityAnalysis/Increases/Increase",new 
 				IncreaseHandler(stack, table, handlerCache));
         
 		root.track("/Laws/LawOrganization/Scene/CriticalityAnalysis/Increases/Increase/Assignee",new 
 				EventAssigneeHandler(stack, table, handlerCache));
 		root.track("/Laws/LawOrganization/Scene/CriticalityAnalysis/Decreases/Decrease",new 
 				DecreaseHandler(stack, table, handlerCache));
 		root.track("/Laws/LawOrganization/Scene/CriticalityAnalysis/Decreases/Decrease/Assignee",new 
 				EventAssigneeHandler(stack, table, handlerCache));
         
         root.track("/Laws/LawOrganization/Role",new
        		 RoleHandler(stack, table, handlerCache));
        
         root.track("/Laws/LawOrganization/Scene/Protocol", new 
        		 ProtocolHandler(stack, table, handlerCache));
         // BEGIN Message
         root.track("/Laws/LawOrganization/Scene/Protocol/Messages/Message",new
                 MessageHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/Protocol/Messages/Message/Content/Entry",new
                 ContentEntryHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/Protocol/Messages/Message/Sender",new
                 SenderHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/Protocol/Messages/Message/Receivers/Receiver",new
                 ReceiverHandler(stack, table, handlerCache));
         // END Message
         root.track("/Laws/LawOrganization/Scene/Protocol/States/State", new 
        		 StateHandler(stack, table, handlerCache));
         root.track("/Laws/LawOrganization/Scene/Protocol/Transitions/Transition", new 
        		 TransitionHandler(stack, table, handlerCache));
         
        root.track("/Laws/LawOrganization/Scene/Protocol/Transitions/Transition/ActivatedNorms/NormRef",new
        		ActivatedNormRefHandler(stack, table, handlerCache));
        root.track("/Laws/LawOrganization/Scene/Protocol/Transitions/Transition/DeactivatedNorms/NormRef",new
        		DeactivatedNormRefHandler(stack, table, handlerCache));
        
        root.track("/Laws/LawOrganization/Scene/Protocol/Transitions/Transition/Constraint",new 
				ConstraintHandler(stack, table, handlerCache));
        
        root.track("/Laws/LawOrganization/Scene/Clock",new
                ClockHandler(stack, table, handlerCache));
        
        root.track("/Laws/LawOrganization/Scene/Clock/Activations/Element",activationElement);
        root.track("/Laws/LawOrganization/Scene/Clock/Deactivations/Element",deactivationElement);
        
        // Norms in the scene context
        root.track("/Laws/LawOrganization/Scene/Norm",new
                NormHandler(stack, table, handlerCache));    
        
        root.track("/Laws/LawOrganization/Scene/Norm/Assignee",new
       		 NormAssigneeHandler(stack, table, handlerCache));
        
        root.track("/Laws/LawOrganization/Scene/Norm/Activations/Element",activationElement);
        root.track("/Laws/LawOrganization/Scene/Norm/Deactivations/Element",deactivationElement);
		 
		root.track("/Laws/LawOrganization/Scene/Action",new 
				ActionHandler(stack, table, handlerCache));
		
		root.track("/Laws/LawOrganization/Scene/Action/Element",activationElement);
		
		root.track("/Laws/LawOrganization/Action",new 
				ActionHandler(stack, table, handlerCache));
		
		root.track("/Laws/LawOrganization/Action/Element",activationElement);

        return root;
    }

    public Hashtable<Id, Object> getHandlerCache() {
        return handlerCache;
    }

}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.14  2006/07/04 21:10:33  mgatti
 * UPDATE LOG: 04/07/2006: XMLaw - versão utilizada no projeto final de programação da Maíra.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.13  2006/05/10 14:29:47  mgatti
 * UPDATE LOG: 10/05/2006: XMLaw.
 * UPDATE LOG: Correções de erros na observação de eventos (clocks, normas e actions)
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.12  2006/05/10 14:02:27  mgatti
 * UPDATE LOG: 10/05/2006: XMLaw.
 * UPDATE LOG: Correções de erros na observação de eventos (clocks, normas e actions)
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.11  2006/05/02 23:33:55  mgatti
 * UPDATE LOG: 02/05/2006:  XMLaw - Actions e Constraints
 * UPDATE LOG: Está dando quando as duas cenas de uma mesma organização estão especificadas (o "saco" dos states e transitions está pegando tudo)
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.10  2006/02/22 20:49:30  mgatti
 * UPDATE LOG: Maíra: 1ª Versão da Integração DimaX & XMLaw
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.9  2006/02/01 16:46:13  mgatti
 * UPDATE LOG: Maíra: Inclusão da Criticalidade - 01/02/2006
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.8  2006/01/16 18:28:29  lfrodrigues
 * UPDATE LOG: inclusao dos handlers para papel de entrada de cena e estados permitidos
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.7  2006/01/13 20:24:38  lfrodrigues
 * UPDATE LOG: CreatorSceneHandler incluido
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.6  2006/01/12 17:50:48  mgatti
 * UPDATE LOG: Maíra: Inclusão da ativação da transição pelas nomas ativadas ou não.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.5  2006/01/12 17:11:42  rbp
 * UPDATE LOG: Colocados os casos de teste pra funcionar.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.4  2006/01/11 19:32:51  rbp
 * UPDATE LOG: Eliminação de erros no parser e no formato do xml
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.3  2006/01/11 17:46:52  mgatti
 * UPDATE LOG: Maíra: Inclusão da norma (Handler, Descritor e Execution)
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.2  2006/01/09 20:06:34  mgatti
 * UPDATE LOG: Maíra: Inclusão dos novos elementos gerados pelo Rose, e modificação de pacotes.
 * UPDATE LOG: obs: os que foram alterados no Rose (ITrigger, por exemplo), não estão sendo incluídos agora.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.1  2005/12/29 16:35:30  rbp
 * UPDATE LOG: Versão da primeira pausa do pair-programming. Paramos depois da conversa com o guga.
 * UPDATE LOG: - Incluido o arquivo Brainstorm de planejamento, que contem os principios e passos de planejamento.
 * UPDATE LOG: - Incluido uma primeira versao da proposta do xml.
 * UPDATE LOG: Revision 1.1 2005/12/05 18:29:48 rbp Versão inicial
 * Revision 1.1 2005/08/17 17:48:14 lfrodrigues instalacao do maven como
 * ferramenta de build automatico Revision 1.2 2005/05/16 09:23:02 guga After
 * 1st phase refactoring - backup Revision 1.1 2005/05/10 16:22:56 guga Inicio
 * Refactoring Revision 1.5 2005/04/29 21:40:55 guga Modificações na estrutura e
 * primeira versao da implementacao do TAC SCM Revision 1.4 2005/02/15 20:56:04
 * rbp Versão da dissertação Revision 1.3 2004/12/16 16:08:35 rbp Included scene
 * lifecycle and also the specification of who can create and participate of a
 * scene. Revision 1.2 2004/12/08 21:58:15 rbp Added constraints, which includes
 * descriptors, parsers, model classes.. Modifications on actions. TAC bug on
 * protocol fixed. Revision 1.1 2004/11/24 01:06:04 rbp Main modifications: -
 * Allow multiple instances of the same scene - Introducing a 3 layer mapping
 * from XML: XML --> Descriptor Layer --> Model Layer - Framework of actions
 * introduced; now it is possible use java code as consequence of some law
 * configuration. - Implementation of a case study in the context of product
 * trading using SWT and JFace ( it requires to configure the eclipse for
 * running the application) - Some bugs were fixed altough I don't remember of
 * them. Revision 1.1 2004/09/22 01:04:38 rbp This version contains severals
 * modifications: - Package renaming - Specification of laws from XML language -
 * Added a Sax based Xml parser for reading from XML - Some hard-coded code were
 * removed and placed in the Config.properties file - Added the simple
 * application example
 ******************************************************************************/
