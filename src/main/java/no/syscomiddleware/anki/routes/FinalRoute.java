package no.syscomiddleware.anki.routes;

import no.syscomiddleware.anki.beans.EventTransformer;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchComponent;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalRoute extends RouteBuilder {
    private Logger logger = LoggerFactory.getLogger(FinalRoute.class);
    private String fileInput;
    private String esOutput;
    private String jettyInput;

    public FinalRoute() {
        this.fileInput = "file:///home/prakhar/Workspace/java/anki-rest/file?noop=true&delay=10000";
        this.esOutput = "elasticsearch-rest://docker-cluster?operation=INDEX";
        this.jettyInput = "jetty:http://0.0.0.0:8080/anki-rest?httpMethodRestrict=POST";
    }


    @Override
    public void configure() throws Exception {
        ElasticsearchComponent elasticsearchComponent = new ElasticsearchComponent();
        elasticsearchComponent.setHostAddresses("localhost:19200");
        getContext().addComponent("elasticsearch-rest", elasticsearchComponent);

        // expose jetty endpoint to listen to POST requests
        // Comment this section if testing locally against the file
        from(this.jettyInput)
                .log(LoggingLevel.INFO, logger, "${headers}")
                // Convert payload to JSONObject
                .process((e) -> {
                    String body = e.getIn().getBody(String.class);
                    JSONObject jsonObj = new JSONObject(body.trim());
                    e.getIn().setBody(jsonObj);
                })


                // Check Type of Json and format data accordingly
                .bean(EventTransformer.class)
                // if the formatted data is not null then push to elastic search
                .choice().when(body().isNotNull())
                    .log(LoggingLevel.INFO, logger, "${body}")
                    .to(this.esOutput)
                .endChoice()
                .end();

// Use below for testing against file under /file directory
//        from(this.fileInput)
//                .routeId("FinalRoute")
//                .streamCaching()
//                .choice()
//                .when(header("CamelFileName").regex("formatted.*txt"))
//                .marshal().string("UTF-8")
//                .split(body().tokenize(System.lineSeparator()))
//                .process((e) -> {
//                    String body = e.getIn().getBody(String.class);
//                    JSONObject jsonObj = new JSONObject(body.trim());
//                    e.getIn().setBody(jsonObj);
//                })
//                .bean(ConsolidatedConverter.class)
//                .choice().when(body().isNotNull())
//                .log(LoggingLevel.INFO, logger, "${body}")
//                .to(this.esOutput)
//                .endChoice()
//                .endChoice()
//                .otherwise()
//                .log(LoggingLevel.INFO, logger, "Invalid file")
//                .endChoice()
//                .end();
    }
}