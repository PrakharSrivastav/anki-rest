package no.syscomiddleware.anki.routes;

import no.syscomiddleware.anki.utils.KafkaEndpointBuilder;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class KafkaProducer extends RouteBuilder {
    private String fileInput;
    private Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer() {
        this.fileInput = "file:///home/prakhar/Workspace/java/anki-rest/file?noop=true&delay=10000&idempotent=false";
    }

    @Override
    public void configure() throws Exception {
        //@formatter:off
        from(this.fileInput)
                .routeId("KafkaProducerRoute")
                .choice()
                    .when(header("CamelFileName").regex("formatted.*txt"))
                        .marshal().string("UTF-8")
                        .split(body().tokenize(System.lineSeparator()))
                        .process((e) -> {
                            String body = e.getIn().getBody(String.class);
                            JSONObject jsonObj = new JSONObject(body.trim());
                            e.getIn().setBody(jsonObj);
                        })

                        .to(this.kafkaEndpoint("CAR_EVENTS", null, null))
                    .endChoice()
                    .otherwise()
                        .log(LoggingLevel.INFO, logger, "Found some other files")
                    .endChoice()
                .end();
        //@formatter:on
    }


    private String kafkaEndpoint(final String topic, final String serilaizerClass, final String deserializerClass) {
        final KafkaEndpointBuilder endpoint = new KafkaEndpointBuilder();
        endpoint.setBroker("localhost:29092");
        endpoint.setClientId("car-event-producer");
        endpoint.setGroupId("car-event-producer-group");
        endpoint.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
        endpoint.setTopic(topic);
        if (serilaizerClass != null) endpoint.setSerializerClass(serilaizerClass);
        if (deserializerClass != null) endpoint.setValueDeserializer(deserializerClass);
        return endpoint.getEndpointUri();
    }
}
