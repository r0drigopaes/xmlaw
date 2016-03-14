/* ****************************************************************
 *   @filename:		AgentJadeCommunication.java
 *   @projectname:  Law
 *   @date:			Jun 14, 2004
 *   @author 		rbp
 * 	 
 *   $Id: AgentJadeCommunication.java,v 1.3 2006/05/06 19:47:11 lfrodrigues Exp $
 * ***************************************************************/
package br.pucrio.inf.les.law.communication.jade;

import br.pucrio.inf.les.law.communication.Message;
import br.pucrio.inf.les.law.util.Constants;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Especialização da classe JadeCommunication para ser utilizado pelos agentes.
 * Essa classe delega as para um agente especial. Esse agente faz o enforcement
 * das mensagens e depois disso envia a mensagem para o destinatário real.
 * 
 * @author rbp
 * @version $Revision: 1.3 $
 */
public class AgentJadeCommunication extends JadeCommunication {

    public AgentJadeCommunication(String name) {
        super(name);
    }

    /**
     * Sobrescreve o método send, para que as mensagens sejam primeiro enviadas
     * para o agente especial.
     * 
     * @see br.pucrio.inf.les.law.shared.agent.communication.ICommunication#send(br.pucrio.inf.les.law.mediator.communication.Message)
     */
    public void send(Message msg) {

        ACLMessage message = super.messageWrapper.transform(msg);
       
        // Só troca o receiver, o papel permanece do receiver não é alterado,
        // assim,
        // o GOD acessa o papel do destinatário facilmente.
        message.clearAllReceiver();
        message.addReceiver(new AID(Constants.MEDIATOR_NAME.getValue(),
                AID.ISLOCALNAME));
        // Log.log.debug("Mensagem interceptada! Delegando para o god.");
        jadeAgent.send(message);
    }

}

/*******************************************************************************
 * LOG DE ALTERAÇÕES: $Log: AgentJadeCommunication.java,v $
 * LOG DE ALTERAÇÕES: Revision 1.3  2006/05/06 19:47:11  lfrodrigues
 * LOG DE ALTERAÇÕES: adaptacao do impacto da transformacao de mensagem de um para varios receptores
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.2  2006/04/25 16:36:23  mgatti
 * LOG DE ALTERAÇÕES: 26/04/2006: Versão com alterações da implementação do broadcast.
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.1  2006/02/17 11:31:18  lfrodrigues
 * LOG DE ALTERAÇÕES: adaptacao e criacao de classes de comunicacao especificas para agente e mediador
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.4  2006/02/01 20:18:45  lfrodrigues
 * LOG DE ALTERAÇÕES: mudificações no pacote model para melhor modularização do projeto em api cliente e servidor
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.3  2006/01/31 17:33:56  lfrodrigues
 * LOG DE ALTERAÇÕES: classe Message movida de pacote model para pacote communication
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.2  2006/01/31 17:29:37  lfrodrigues
 * LOG DE ALTERAÇÕES: renomeação do pacote agente para mediator
 * LOG DE ALTERAÇÕES: renomeação do pacote clientapi para agent
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.1  2006/01/09 20:06:34  mgatti
 * LOG DE ALTERAÇÕES: Maíra: Inclusão dos novos elementos gerados pelo Rose, e modificação de pacotes.
 * LOG DE ALTERAÇÕES: obs: os que foram alterados no Rose (ITrigger, por exemplo), não estão sendo incluídos agora.
 * LOG DE ALTERAÇÕES:
 * LOG DE ALTERAÇÕES: Revision 1.1  2005/12/05 18:29:48  rbp
 * LOG DE ALTERAÇÕES: Versão inicial
 * LOG DE ALTERAÇÕES: Revision 1.1
 * 2005/08/17 17:48:14 lfrodrigues instalacao do maven como ferramenta de build
 * automatico Revision 1.1 2005/05/10 16:22:56 guga Inicio Refactoring Revision
 * 1.4 2004/12/14 20:59:41 rbp JadeCommunication now just delegates to jade
 * agent Revision 1.3 2004/11/24 01:06:04 rbp Main modifications: - Allow
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
 * application example Revision 1.2 2004/07/14 21:08:16 rbp Inserindo alterações
 * para permitir multiplas organizações, cenários, não-determinismo etc.
 * Revision 1.1 2004/06/24 22:42:51 rbp Após primeira reengenharia de pacotes e
 * antes de gerar modelos Revision 1.2 2004/06/24 22:09:04 rbp Versão de mudança
 * para reengenharia final, introdução estrutura inicial para a máquina de
 * estado Revision 1.1 2004/06/16 01:58:25 rbp Primeiros arquivos a entrar no
 * CVS
 ******************************************************************************/
