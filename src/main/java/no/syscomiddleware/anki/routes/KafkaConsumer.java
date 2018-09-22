package no.syscomiddleware.anki.routes;

import no.syscomiddleware.anki.beans.EventTransformer;
import no.syscomiddleware.anki.utils.KafkaEndpointBuilder;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchComponent;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumer extends RouteBuilder {


    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private String esOutput;

    public KafkaConsumer() {
        this.esOutput = "elasticsearch-rest://docker-cluster?operation=INDEX";
    }

    @Override
    public void configure() throws Exception {

        ElasticsearchComponent elasticsearchComponent = new ElasticsearchComponent();
        elasticsearchComponent.setHostAddresses("localhost:19200");
        getContext().addComponent("elasticsearch-rest", elasticsearchComponent);

        from(this.kafkaEndpoint("NOTIFICATION", null, null))
                .routeId("KafkaConsumerRoute")
                .process((e) -> {
                    String body = e.getIn().getBody(String.class);
                    JSONObject jsonObj = new JSONObject(body.trim());
                    e.getIn().setBody(jsonObj);
                })
//                .convertBodyTo(JSONObject.class)
                .bean(EventTransformer.class)
                .choice()
                .when(body().isNotNull())
                    .log(LoggingLevel.INFO, logger, "THIS IS ES BOSY ${body}")
                    .to(this.esOutput)
                .otherwise()
                    .log(LoggingLevel.INFO, logger, "EMPTY BODY")
                .endChoice()
//                .end()
        ;
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
