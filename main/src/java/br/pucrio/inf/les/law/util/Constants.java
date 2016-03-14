package br.pucrio.inf.les.law.util;


public enum Constants {

    MEDIATOR_NAME("agent.mediator.name"), MEDIATOR_ID("agent.mediator.id"), MEDIATOR_COMMUNICATION_CLASS(
            "agent.mediator.communication.class"), AGENT_COMMUNICATION_CLASS("agent.user.communication.class");

    private Constants(String key) {
        this.key = key;
        this.value = Config.getInstance().get(key);
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    private String key;

    private String value;

}
