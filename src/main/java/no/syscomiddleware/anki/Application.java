package no.syscomiddleware.anki;

import no.syscomiddleware.anki.routes.KafkaConsumer;
import no.syscomiddleware.anki.routes.KafkaProducer;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        final Main main = new Main();
        main.addMainListener(new Events());

        // Camel-ELK stack Integration routes
//        main.addRouteBuilder(new FinalRoute());

        // Camel-Kafka-ELK stack Integraiton routes
        main.addRouteBuilder(new KafkaProducer());
        main.addRouteBuilder(new KafkaConsumer());

        try {
            main.run();
        } catch (Exception e) {
            logger.error("Error starting Camel Application ", e);
            e.printStackTrace();
        }
    }

    private static class Events extends MainListenerSupport {
        private static Logger logger = LoggerFactory.getLogger(Events.class);

        @Override
        public void afterStart(final MainSupport main) {logger.info("Camel app is now started!");}

        @Override
        public void beforeStop(final MainSupport main) {logger.info("Camel app is shutting down!");}
    }

}
