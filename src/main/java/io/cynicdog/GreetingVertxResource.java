package io.cynicdog;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
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

        // serve entry page to the requests of root path
        router.route().handler(StaticHandler.create());
        router.get("/").handler(routingContext -> { routingContext.response().putHeader("Content-Type", "text/html").sendFile( "src/main/webui/quinoa.html"); });

        // kubectl exec -it vertx-quarkus-demo -- /bin/bash
        // curl http://vertx-quarkus-demo/greeting
        router.get("/greeting")
                .handler(ctx -> ctx.response().end("Greeting from Vertx in Quarkus 👾"));
    }
}
