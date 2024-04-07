package io.cynicdog;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class GreetingVertxResource {

    @Inject
    Vertx vertx;

    static final Logger logger = Logger.getLogger(GreetingVertxResource.class);

    public void init(@Observes Router router) {

        // register logging filter
        router.route().handler(ctx -> {
            logger.info("Request path: " + ctx.request().path());
            ctx.next();
        });

        // kubectl exec -it vertx-quarkus-demo -- /bin/bash
        // curl http://vertx-quarkus-demo/greeting
        router.get("/greeting")
                .handler(ctx -> ctx.response().end("Greeting from Vertx in Quarkus."));
    }
}
