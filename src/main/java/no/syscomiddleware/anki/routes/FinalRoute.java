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
        this.fileInput = "file:///home/prakhar/Workspace/java/anki-rest/file?noop=true&delay=10000&idempotent=false";
        this.esOutput = "elasticsearch-rest://docker-cluster?operation=INDEX";
        this.jettyInput = "jetty:http://0.0.0.0:8080/anki-rest?httpMethodRestrict=POST";
    }


    @Override
    public void configure() throws Exception {
        final ElasticsearchComponent elasticsearchComponent = new ElasticsearchComponent();
        elasticsearchComponent.setHostAddresses("localhost:19200");
        getContext().addComponent("elasticsearch-rest", elasticsearchComponent);


        //@formatter:off
        from(this.fileInput)
                .routeId("FinalRoute")
                .streamCaching()
                .choice()
                    .when(header("CamelFileName").regex("formatted.*txt"))
                        .marshal().string("UTF-8")
                        .split(body().tokenize(System.lineSeparator()))
                        .process((e) -> {
                            String body = e.getIn().getBody(String.class);
                            JSONObject jsonObj = new JSONObject(body.trim());
                            e.getIn().setBody(jsonObj);
                        })
                        .bean(EventTransformer.class)
                        .choice()
                            .when(body().isNotNull())
                                .log(LoggingLevel.INFO, logger, "${body}")
                                .to(this.esOutput)
                            .otherwise()
                                .log(LoggingLevel.INFO,logger, "Nothing to stream to ES")
                        .endChoice()
                    .otherwise()
                        .log(LoggingLevel.INFO, logger, "Invalid file")
                .endChoice()
                .end();
        //@formatter:on



        //  expose jetty endpoint to listen to POST requests
        //  Comment this section if testing locally against the file
        /*
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
        */



    }
}