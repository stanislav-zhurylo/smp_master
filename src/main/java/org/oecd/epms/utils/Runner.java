package org.oecd.epms.utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import java.util.function.Consumer;
 
public class Runner {
     
    private static final String RESOURCES_JAVA_DIR = "/src/main/resources/";  
    
    public static void runServer(Class<? extends AbstractVerticle> clazz, DeploymentOptions deploymentOptions) { 
        VertxOptions vertxOptions = new VertxOptions().setClustered(false);
        deployVerticle(RESOURCES_JAVA_DIR, clazz.getName(), vertxOptions, deploymentOptions);
    } 
    
    private static void deployVerticle(String webrootParentDir, String verticleId, VertxOptions options,
            DeploymentOptions deploymentOptions) {
        if (options == null) { 
            options = new VertxOptions();
        }
        System.setProperty("vertx.cwd", webrootParentDir);
        Consumer<Vertx> runner = vertx -> {
            try {
                if (deploymentOptions != null) {
                    vertx.deployVerticle(verticleId, deploymentOptions);
                }
                else {
                    vertx.deployVerticle(verticleId);
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (options.isClustered()) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                }
                else {
                    res.cause().printStackTrace();
                }
            });
        }
        else {
            Vertx vertx = Vertx.vertx(options);
            runner.accept(vertx);
        }
    }
}
