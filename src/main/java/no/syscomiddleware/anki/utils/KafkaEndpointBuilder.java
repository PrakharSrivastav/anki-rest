package no.syscomiddleware.anki.utils;

public final class KafkaEndpointBuilder implements EndpointBuilder {
    private String protocol;
    private String topic;
    private String broker;
    private String groupId;
    private String clientId;
    private String key;
    private String autoOffsetReset;
    private String serializerClass;
    private String valueDeserializer;
    private String autoCommitEnable;

    public KafkaEndpointBuilder() {this.protocol = "kafka:";}

    public void setKey(final String key) { this.key = key; }
    public void setTopic(final String topic) { this.topic = topic; }
    public void setBroker(final String broker) { this.broker = broker; }
    public void setGroupId(final String groupId) { this.groupId = groupId; }
    public void setClientId(final String clientId) { this.clientId = clientId; }
    public void setSerializerClass(final String serializerClass) { this.serializerClass = serializerClass; }
    public void setAutoOffsetReset(final String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; }
    public void setAutoCommitEnable(final String autoCommitEnable) { this.autoCommitEnable = autoCommitEnable; }
    public void setValueDeserializer(final String valueDeserializer) { this.valueDeserializer = valueDeserializer; }


    public String getEndpointUri() {
        StringBuilder endpointUri = new StringBuilder();
        if (this == null)
            throw new IllegalArgumentException("Endpoint Builder is not instantiated");
        if (this.broker == null || this.broker.isEmpty())
            throw new IllegalArgumentException("Broker for kafka endpoint not defined");
        if (this.topic == null || this.topic.isEmpty())
            throw new IllegalArgumentException("Topic for kafka not defined");

        endpointUri.append(this.protocol);
        endpointUri.append(this.topic);
        endpointUri.append("?brokers=");
        endpointUri.append(this.broker);

        if (this.groupId != null && !this.groupId.isEmpty()) {
            endpointUri.append("&groupId=");
            endpointUri.append(this.groupId);
        }

        if (this.clientId != null && !this.clientId.isEmpty()) {
            endpointUri.append("&clientId=");
            endpointUri.append(this.clientId);
        }

        if (this.key != null && !this.key.isEmpty()) {
            endpointUri.append("&key=");
            endpointUri.append(this.key);
        }

        if (this.autoOffsetReset != null && !this.autoOffsetReset.trim().isEmpty()) {
            endpointUri.append("&autoOffsetReset=");
            endpointUri.append(this.autoOffsetReset);
        }

        if (this.serializerClass != null && !this.serializerClass.isEmpty()) {
            endpointUri.append("&serializerClass=");
            endpointUri.append(this.serializerClass);
        }

        if (this.valueDeserializer != null && !this.valueDeserializer.isEmpty()) {
            endpointUri.append("&valueDeserializer=");
            endpointUri.append(this.valueDeserializer);
        }

        if (this.autoCommitEnable != null && !this.autoCommitEnable.isEmpty()) {
            endpointUri.append("&autoCommitEnable=");
            endpointUri.append(this.autoCommitEnable);
        }
        return endpointUri.toString();
    }
}