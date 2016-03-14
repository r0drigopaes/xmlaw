/* ****************************************************************
 *   @filename:		LawExeception.java
 *   @projectname:  Law
 *   @date:			Jul 13, 2004
 *   @author 		rbp - LES (PUC-Rio)
 * 	 
 *   $Id$
 * ***************************************************************/
package br.pucrio.inf.les.law.model;

/**
 * @author rbp - LES (PUC-Rio)
 * @version $Revision$
 */
public class LawException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final int SCENE_DOES_NOT_EXIST = 0;

    public static final int NORM_DISABLED = 1;

    public static final int CLASS_NOT_FOUND = 2;

    public static final int CONSTRUCTOR_INVOCATION = 3;

    public static final int ILLEGAL_ACCESS = 4;

    public static final int MISSING_TYPE = 5;

    public static final int INVALID_PROTOCOL_SPECIFICATION = 6;

    public static final int SCENE_CREATION_NOT_ALLOWED = 7;

    public static final int AGENT_IS_NOT_PARTICIPANT = 8;

    public static final int AGENT_CANNOT_PARTICIPATE = 9;

    public static final int CLASS_NOT_AS_EXPECTED = 10;

    public static final int ACTION_EXECUTION_ERROR = 11;

    public static final int CLASS_NO_SUCH_METHOD = 12;

    public static final int INVALID_STATE_TYPE = 13;

    public static final int PARENT_EXECUTION_NOT_FOUND = 14;

    public static final int MISSING_CONFIGURATION = 15;

    public static final int MISSING_IMPLEMENTATION = 16;

    public static final int MASKS_INCONSISTENT = 17;

    public static final int MISSING_EXECUTION_CONTEXT = 18;

    public static final int MISSING_EVENT = 19;
    
    public static final int MISSING_DESCRIPTOR = 20;
    
    public static final int ROLE_NOT_DECLARED = 21;
    
    public static final int INVALID_CLOCK_TYPE = 22;
    
    public static final int MISSING_TIME_TO_LIVE = 23;

	public static final int INVALID_NORM_TYPE = 24;

	public static final int ROLES_DOESNT_MATCH = 25;
    
    public static final int INVALID_NORM = 26;
    
    public static final int INCONSISTENT_ROLES_LIMIT_IN_PROTOCOL_STATE = 27;
    
    public static final int INVALID_EVENT_TYPE_SPECIFIED = 28;
    
    public static final int MISSING_MESSAGE_IN_EVENT_CONTENT = 29;
    
    public static final int MISSING_ROLEREFERENCE_IN_EVENT_CONTENT = 30;
    
    public static final int EVENT_TYPE_DOESNT_EXIST = 31;
    
    private String message;

    private String tip;

    private int reason;

    

    public LawException(String message, int reason) {
        this.message = message;
        this.reason = reason;
    }

    public LawException(String message, String tip, int reason) {
        this.message = message;
        this.reason = reason;
        this.tip = tip;
    }

    public String getMessage() {
        return message;
    }

    public int getReason() {
        return reason;
    }

    public String getTip() {
        return tip;
    }

    public String toString() {
        return "lawException(text(" + message + "),tip(" + tip
                + "),reason-code(" + reason + "))";
    }
}

/*******************************************************************************
 * UPDATE LOG: $Log$
 * UPDATE LOG: Revision 1.7  2006/05/04 20:48:44  mgatti
 * UPDATE LOG: 04/05/2006: Alteração da implementação de Actions
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.6  2006/02/22 20:49:30  mgatti
 * UPDATE LOG: Maíra: 1ª Versão da Integração DimaX & XMLaw
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.5  2006/01/16 18:37:11  lfrodrigues
 * UPDATE LOG: inclusao de excecao para limite inconsistentes de papeis em cena
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.4  2006/01/13 17:33:58  rbp
 * UPDATE LOG: casos de teste para o norm execution
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.3  2006/01/11 17:46:52  mgatti
 * UPDATE LOG: Maíra: Inclusão da norma (Handler, Descritor e Execution)
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.2  2005/12/29 16:35:30  rbp
 * UPDATE LOG: Versão da primeira pausa do pair-programming. Paramos depois da conversa com o guga.
 * UPDATE LOG: - Incluido o arquivo Brainstorm de planejamento, que contem os principios e passos de planejamento.
 * UPDATE LOG: - Incluido uma primeira versao da proposta do xml.
 * UPDATE LOG:
 * UPDATE LOG: Revision 1.1  2005/12/05 18:29:48  rbp
 * UPDATE LOG: Versão inicial
 * UPDATE LOG: Revision 1.1 2005/08/17 17:48:14
 * lfrodrigues instalacao do maven como ferramenta de build automatico Revision
 * 1.1 2005/05/10 16:22:57 guga Inicio Refactoring Revision 1.7 2005/04/29
 * 21:40:55 guga Modificações na estrutura e primeira versao da implementacao do
 * TAC SCM Revision 1.6 2005/02/15 20:56:04 rbp Versão da dissertação Revision
 * 1.5 2004/12/16 16:08:35 rbp Included scene lifecycle and also the
 * specification of who can create and participate of a scene. Revision 1.4
 * 2004/12/08 21:58:15 rbp Added constraints, which includes descriptors,
 * parsers, model classes.. Modifications on actions. TAC bug on protocol fixed.
 * Revision 1.3 2004/11/24 01:06:04 rbp Main modifications: - Allow multiple
 * instances of the same scene - Introducing a 3 layer mapping from XML: XML -->
 * Descriptor Layer --> Model Layer - Framework of actions introduced; now it is
 * possible use java code as consequence of some law configuration. -
 * Implementation of a case study in the context of product trading using SWT
 * and JFace ( it requires to configure the eclipse for running the application) -
 * Some bugs were fixed altough I don't remember of them. Revision 1.1
 * 2004/09/22 01:04:38 rbp This version contains severals modifications: -
 * Package renaming - Specification of laws from XML language - Added a Sax
 * based Xml parser for reading from XML - Some hard-coded code were removed and
 * placed in the Config.properties file - Added the simple application example
 * Revision 1.1 2004/07/14 21:08:16 rbp Inserindo alterações para permitir
 * multiplas organizações, cenários, não-determinismo etc.
 ******************************************************************************/
