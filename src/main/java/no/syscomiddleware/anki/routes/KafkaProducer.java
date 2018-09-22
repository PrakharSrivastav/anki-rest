package no.syscomiddleware.anki.routes;

import no.syscomiddleware.anki.utils.KafkaEndpointBuilder;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.kafka.common.serialization.Serdes;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducer extends RouteBuilder {
    private String fileInput;
    private Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer() {
        this.fileInput = "file:///home/prakhar/Workspace/java/anki-rest/file?noop=true&delay=10000";
    }

    @Override
    public void configure() throws Exception {
        from(this.fileInput)
                .routeId("KafkaProducerRoute")
//                .log(LoggingLevel.INFO, logger, "there")
                .choice()
                .when(header("CamelFileName").regex("formatted.*txt"))
//                .log(LoggingLevel.INFO, logger, "heree")
                .marshal().string("UTF-8")
                .split(body().tokenize(System.lineSeparator()))
                .process((e) -> {
                    String body = e.getIn().getBody(String.class);
                    JSONObject jsonObj = new JSONObject(body.trim());
                    e.getIn().setBody(jsonObj);
                })
                .log(LoggingLevel.INFO, logger, "${body}")
//                .log(LoggingLevel.INFO, logger, "${body}")
                .to(this.kafkaEndpoint("NOTIFICATION", String.class.getCanonicalName(), null))


                .endChoice()

                .log(LoggingLevel.INFO, logger, "${headers}");
    }


    private String kafkaEndpoint(final String topic, final String serilaizerClass, final String deserializerClass) {
        final KafkaEndpointBuilder endpoint = new KafkaEndpointBuilder();
        endpoint.setBroker("localhost:29092");
        endpoint.setClientId("clientId");
        endpoint.setGroupId("groupId");
        endpoint.setKey("key");
        endpoint.setTopic(topic);
//        if (serilaizerClass != null)
//            endpoint.setSerializerClass(Serdes.String().getClass().getCanonicalName());

        if (deserializerClass != null)
            endpoint.setValueDeserializer(deserializerClass);
        return endpoint.getEndpointUri();
    }
}
