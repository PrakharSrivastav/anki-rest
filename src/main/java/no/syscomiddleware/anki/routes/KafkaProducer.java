package no.syscomiddleware.anki.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
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
                .choice()
                    .when(header("CamelFileName").regex("formatter.*txt"))
//                        .to()
                    .endChoice()
                .log(LoggingLevel.INFO, logger, "${headers}");
    }
}
