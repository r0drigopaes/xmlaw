/* ****************************************************************
 *   @filename:     Subject.java
 *   @projectname:  Law
 *   @date:         Jun 23, 2004
 *   @author        rbp
 *
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.event;

import br.pucrio.inf.les.law.model.Id;
import br.pucrio.inf.les.law.model.LawException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rodrigopaes.utils.binary.BinaryOperations;

/**
 * <p>
 * Todos os objetos que quiserem gerar eventos, devem fazê-lo através do método
 * fireEvent() desta classe. Desta forma, os observers desta classe serão
 * avisados quando estes eventos ocorrem.
 * </p>
 * <p>
 * O gerenciador de eventos da aplicação é uma instância desta classe.
 * </p>
 * 
 * @alias Subject
 * @author rbp & guga - LES (PUC-Rio)
 * @version $Revision$
 */
public class Subject {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(Subject.class);

    /**
     * Armazena todos os observers que serão avisados quando um evento ocorrer.
     * 
     * @associates <{br.pucrio.inf.les.law.protocol.event.IObserver}>
     * @label observers
     * @link aggregation
     * @supplierCardinality 0..*
     */
    private Hashtable<Integer, ArrayList<IObserver>> observers;

    /**
     * Queue of events that must be generated
     */
    private Queue<Event> events;

    /**
     * Cria um gerenciador de eventos.
     * 
     * @author rbp
     */
    public Subject() {
        this.observers = new Hashtable<Integer, ArrayList<IObserver>>();
        events = new LinkedList<Event>();
        
        
    }
    
    

    /**
     * Adiciona um observador de eventos neste Subject.<br>
     * Este observador será avisado todas as vezes que um evento ocorrer.
     */
    public void attachObserver(IObserver observer, int eventMask) {
        for (int i = 1; i <= eventMask; i = i * 2) {
            if (BinaryOperations.contains(i, eventMask)) {
                Integer eventType = new Integer(i);
                ArrayList<IObserver> vector = observers.get(eventType);
                if (vector == null) {
                    vector = new ArrayList<IObserver>();
                    observers.put(eventType, vector);
                }
                if (!vector.contains(observer)) {
                    vector.add(observer);
                }
            }
        }
    }

    /**
     * Remove um observador de eventos deste Subject.
     * 
     * @hipothesis o observer tem que ter sido adicionado a este event manager
     *             anteriormente.
     */
    public void detachObserver(IObserver observer, int eventMask) {
        for (int i = 1; i <= eventMask; i = i * 2) {
            if (BinaryOperations.contains(i, eventMask)) {
                Integer eventType = new Integer(i);
                ArrayList<IObserver> vector = observers.get(eventType);
                if (vector != null) {
                    vector.remove(observer);
                }
            }
        }
    }
    
//    public void detachAllObservers(int eventMask){
//    	for (int i = 1; i <= eventMask; i = i * 2) {
//            if (BinaryOperations.contains(i, eventMask)) {
//                Integer eventType = new Integer(i);
//                ArrayList<IObserver> vector = observers.get(eventType);
//                if (vector != null) {
//                	for (IObserver observer : vector) {
//                		vector.remove(observer);
//                	}
//                }
//            }
//        }
//    }
    
    

    /**
     * Dispara um evento. (Asyncrhonous calling)
     * 
     * @param event
     *            O evento a ser disparado.
     * @pos todos os observers deste Subject serão notificados da ocorrência
     *      deste evento.
     * @author rbp
     */
    public void fireEvent(Event event) {
        events.add(event);
        fire();
    }

    private void fire() {
        while (events.size() > 0) {
            Event event = events.poll();
            int eventType = event.getType();
            ArrayList<IObserver> vector = observers.get(new Integer(eventType));
            if (vector != null) {
                for (int i = 0; i < vector.size(); i++) {
                    IObserver anObserver = vector.get(i);
                    try {
                        anObserver.update(event);
                    } catch (LawException e1) {
                        LOG.error("Error while processing event. Error Detais: "
                                + e1.getMessage());
                    }
                }
            }
        }
    }



	public List<IObserver> getObservers() {
		ArrayList<IObserver> result = new ArrayList<IObserver>();
		for (Integer key : observers.keySet()) {
			result.addAll( observers.get(key) );
		}
		return result;
	}
    
}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.5  2006/07/04 21:10:33  mgatti
 * UPDATE LOG: 04/07/2006: XMLaw - versão utilizada no projeto final de programação da Maíra.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.4  2006/04/25 16:36:23  mgatti
 * UPDATE LOG: 26/04/2006: Versão com alterações da implementação do broadcast.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.3  2006/01/13 17:33:58  rbp
 * UPDATE LOG: casos de teste para o norm execution
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.2  2006/01/10 16:29:19  mgatti
 * UPDATE LOG: Maíra: Retirada do EventManager e do ITrigger.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.1  2005/12/05 18:29:48  rbp
 * UPDATE LOG: Versão inicial
 * UPDATE LOG: Revision 1.1 2005/08/17 17:48:14
 * lfrodrigues instalacao do maven como ferramenta de build automatico Revision
 * 1.1 2005/05/10 16:22:56 guga Inicio Refactoring Revision 1.1 2005/04/29
 * 21:40:57 guga Modificações na estrutura e primeira versao da implementacao do
 * TAC SCM Revision 1.3 2004/11/24 01:06:04 rbp Main modifications: - Allow
 * multiple instances of the same scene - Introducing a 3 layer mapping from
 * XML: XML --> Descriptor Layer --> Model Layer - Framework of actions
 * introduced; now it is possible use java code as consequence of some law
 * configuration. - Implementation of a case study in the context of product
 * trading using SWT and JFace ( it requires to configure the eclipse for
 * running the application) - Some bugs were fixed altough I don't remember of
 * them. Revision 1.1 2004/09/22 01:04:38 rbp This version contains severals
 * modifications: - Package renaming - Specification of laws from XML language -
 * Added a Sax based Xml parser for reading from XML - Some hard-coded code were
 * removed and placed in the Config.properties file - Added the simple
 * application example Revision 1.5 2004/09/10 17:49:44 rbp Main modifications:
 * Message is not an event anymore. The information can be carried through the
 * InfoCarrier object. TriggerManager complete modification. Now it allows
 * multiple norm and clock instances. Revision 1.4 2004/07/14 21:08:16 rbp
 * Inserindo alterações para permitir multiplas organizações, cenários,
 * não-determinismo etc. Revision 1.3 2004/07/06 16:53:29 guga atualizacao
 * subjects e observer por identificador Revision 1.2 2004/06/30 19:44:20 guga
 * Atualização execução paralela Revision 1.1 2004/06/29 02:30:02 rbp
 * Melhoramentos na interface gráfica: criação da barra de delay, arco no estado
 * final, problema do circulo que estava sendo desenhando com buracos etc.
 * Mudanças na documentação. Revision 1.1 2004/06/28 01:23:09 rbp Primeira
 * versão da interface gráfica de monitoramento do protocolo. Revision 1.2
 * 2004/06/26 23:34:28 rbp Melhorias na documentação do Código. Especificação do
 * cenário de testes. Modificações no Log do sistema. Algumas correções em
 * classes como Clock que estava implementando a interface Runnable de uma forma
 * não muito segura.
 ******************************************************************************/
