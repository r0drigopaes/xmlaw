package br.pucrio.inf.les.law.communication;

public class MessageContentConstants {
		
		// COMMANDS FROM AGENT TO MEDIATOR		
		public static final String CMD_ADD_LAW 						= "addLaw";
		public static final String CMD_ENTER_ORGANIZATION 			= "enterOrg";
		public static final String CMD_START_SCENE 					= "startScene";
		public static final String CMD_ENTER_SCENE 					= "enterScene";
		public static final String CMD_LIST_RUNNING_SCENES			= "listRunningScenes";
		public static final String CMD_LIST_ROLES					= "listRoles";
		public static final String CMD_PERFORM_ROLE					= "performRole";
		public static final String CMD_QUIT_ORGANIZATION			= "quitOrg";
		public static final String CMD_START_EXTERNAL_COMMAND		= "startExternalCommand";
		
		// MESSAGE CONTENT KEYS 
		public static final String KEY_COMMAND						= "command";
		public static final String KEY_ORGANIZATION_NAME			= "organization";
		public static final String KEY_ORGANIZATION_EXECUTION_ID	= "orgExecutionId";
		public static final String KEY_SCENE_NAME					= "scene";
		public static final String KEY_SCENE_EXECUTION_ID			= "sceneExecutionId";
	    public static final String KEY_ROLE_NAME					= "role";
		public static final String KEY_LAW_URL						= "lawUrl";		
		public static final String KEY_TOTAL						= "total";
		public static final String KEY_TXT_MESSAGE					= "txtMessage";
		public static final String KEY_FAILURE						= "keyFailure";
		
		// PROTOCOL CONTENT
		public static final String MEDIATOR_PROTOCOL				= "mediator-protocol";
		
}
