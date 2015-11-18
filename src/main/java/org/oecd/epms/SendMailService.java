package org.oecd.epms;

import static org.oecd.epms.configuration.ConfigurationBuilder.toProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.oecd.epms.configuration.ConfigurationBuilder;
import org.oecd.epms.transfer.MessageDTS;
import org.oecd.epms.transfer.StatusDTS;
import org.oecd.epms.transfer.StatusDTS.DispatchStatus;
import org.oecd.epms.utils.Runner;
import org.oecd.epms.validation.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendMailService extends AbstractVerticle { 
	private static final Logger log = LoggerFactory.getLogger(SendMailService.class);
    
    public static final int SERVER_PORT = 9124;
    public static final String VERTX_EVENTBUS_ENTRYPOINT = "/eventbus/*";
    public static final String MESSAGE_CONSUME_ADDRESS = "epms.email.in";
    public static final String MESSAGE_PUBLISH_ADDRESS = "epms.email.out";
    
    public final static String EMAIL_TRANSFER_SUCCESS = "You email was sent successfully!";
    public final static String EMAIL_TRANSFER_ERROR = "Your email could not be sent, please try again later";
     
    private ObjectMapper mapper = new ObjectMapper();
    private SendMailServiceSupport sendMailServiceSupport;
     
    public static void main(String[] args) { 
    	log.info("Starting server...");
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()  
            .withParam("mail.smtp.auth", "true")
            .withParam("mail.smtp.starttls.enable", "true")
            .withParam("mail.smtp.ssl.trust", "smtp.gmail.com")
            .withParam("mail.smtp.host", "smtp.gmail.com")
            .withParam("mail.smtp.port", "587")
            .withEmailFrom("stas2115@gmail.com")
            .withUsername("stas2115@gmail.com")
            .withPassword(""); 
        Runner.runServer(SendMailService.class, configurationBuilder.toDeploymentOptions()); 
    }    
    
    @Override
    public void start() throws Exception {
        sendMailServiceSupport = new SendMailServiceSupport(
                toProperties(context.config()));
        Router router = Router.router(vertx);
        router.route(VERTX_EVENTBUS_ENTRYPOINT).handler(retrieveSockJSHandler());
        router.route().handler(StaticHandler.create());
        
        vertx.eventBus().<JsonObject> consumer(MESSAGE_CONSUME_ADDRESS)
            .handler(retrieveMessageConsumerHandler());
        
        vertx.createHttpServer().requestHandler(router::accept).listen(SERVER_PORT);
        
        log.info("EventBus message consume address is: " + MESSAGE_CONSUME_ADDRESS);
        log.info("EventBus message publish address is: " + MESSAGE_PUBLISH_ADDRESS);
        log.info("Server has been started at: http://localhost:" + String.valueOf(SERVER_PORT));
    }
    
    @Override
    public void stop() throws Exception {
    	log.info("Server has been stopped");
    } 
    
    Handler<Message<JsonObject>> retrieveMessageConsumerHandler() {
        
        /**
         * Due to email delivery is time costly operation, we have to organize its execution in
         * particular thread, executeBlocking does it for us.
         */
        return message -> vertx.<String> executeBlocking(future -> {
        	String rawRequestMessage = message.body().toString();
            try {  
                MessageDTS messageDts = mapper.readValue(rawRequestMessage, MessageDTS.class); 
                sendMailServiceSupport.validateAndSendMessage(messageDts); 
                future.complete(); 
                log.info("Request message [" + rawRequestMessage + "] has been processed");
            }
            catch (Exception exception) {  
                future.fail(exception); 
            }
        }, result -> {
            try {
                vertx.eventBus().send(MESSAGE_PUBLISH_ADDRESS, retrieveStatusMessageAsString(result));
            }
            catch (Exception exception) { 
                exception.printStackTrace(); 
            }
        });
    }
    
    private String retrieveStatusMessageAsString(AsyncResult<String> result) 
            throws JsonProcessingException {
        StatusDTS status = null;
        if(result.succeeded()) {
            status = new StatusDTS()
                .withDispatchStatus(DispatchStatus.SUCCESS)
                .withMessage(EMAIL_TRANSFER_SUCCESS);
        } else {
            if(result.cause() instanceof ValidationException) {
                status = new StatusDTS()
                    .withDispatchStatus(DispatchStatus.ERROR)
                    .withMessage(result.cause().getMessage()); 
            } else {
                status = new StatusDTS()
                    .withDispatchStatus(DispatchStatus.ERROR)
                    .withMessage(EMAIL_TRANSFER_ERROR)
                    .withStackTrace(ExceptionUtils.getStackTrace(result.cause()));
            }
            
        }
        return mapper.writeValueAsString(status);
    } 
    
    private SockJSHandler retrieveSockJSHandler() {
        return SockJSHandler.create(vertx).bridge(
                prepareBridgeOptions(MESSAGE_CONSUME_ADDRESS, MESSAGE_PUBLISH_ADDRESS));
    }
    
    private BridgeOptions prepareBridgeOptions(String msgConsumeAddress, String msgPublishAddress) {
        BridgeOptions bridgeOptions = new BridgeOptions();
        bridgeOptions.addInboundPermitted(new PermittedOptions().setAddress(msgConsumeAddress));
        bridgeOptions.addOutboundPermitted(new PermittedOptions().setAddress(msgPublishAddress));
        return bridgeOptions;
    }
}
