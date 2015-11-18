package org.oecd.epms;

import static org.oecd.epms.validation.ValidationSupport.MSG_EMAIL_CANT_BE_EMPTY;
import static org.oecd.epms.validation.ValidationSupport.MSG_MESSAGE_BODY_CANT_BE_EMPTY;
import static org.oecd.epms.validation.ValidationSupport.MSG_SUBJECT_CANT_BE_EMPTY;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.oecd.epms.transfer.MessageDTS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(VertxUnitRunner.class)
public class TestSendMailService {
    
    private Vertx vertx;
    private EventBus eventBus;
    private ObjectMapper mapper = new ObjectMapper(); 
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        vertx.deployVerticle(SendMailService.class.getName(), context.asyncAssertSuccess());
    }
     
    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    } 
      
    @Test
    public void testMessageValidationWhenAllFieldsEmpty(TestContext context) throws InterruptedException {  
        try {
            eventBus.<String> consumer(SendMailService.MESSAGE_PUBLISH_ADDRESS).handler(message -> {
                JsonObject statusJson = new JsonObject(message.body());
                String statusMessage = statusJson.getString("message");
                context.assertTrue(StringUtils.isNotEmpty(statusMessage));
                context.assertTrue(statusMessage.contains(MSG_EMAIL_CANT_BE_EMPTY));
                context.assertTrue(statusMessage.contains(MSG_SUBJECT_CANT_BE_EMPTY));
                context.assertTrue(statusMessage.contains(MSG_MESSAGE_BODY_CANT_BE_EMPTY));
            });
            eventBus.send(SendMailService.MESSAGE_CONSUME_ADDRESS, 
                    new JsonObject(mapper.writeValueAsString(new MessageDTS())));  
            await(); 
        } 
        catch (JsonProcessingException e) {
            context.fail();
        }
    } 
    
    @Test
    public void testSMTPConnectionError() throws Exception {  
        thrown.expect(MessagingException.class);
        SendMailServiceSupport sendMailSupport = 
                new SendMailServiceSupport(retrieveFakeProperties());
        sendMailSupport.validateAndSendMessage(retrieveValidMessageDts()); 
    } 
    
    private Properties retrieveFakeProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "none");
        properties.put("mail.smtp.starttls.enable", "none");
        properties.put("mail.smtp.ssl.trust", "none");
        properties.put("mail.smtp.host", "none");
        properties.put("mail.smtp.port", "none");
        properties.put("emailFrom", "none");
        properties.put("username", "none");
        properties.put("password", "none");
        return properties;
        
    }
    
    private MessageDTS retrieveValidMessageDts(){
        MessageDTS messageDts = new MessageDTS();
        messageDts.setRecipientEmail("example@site.com");
        messageDts.setSubject("Test Subject");
        messageDts.setBody("Test Message Body");
        return messageDts;
    }
    
    private void await() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);
    }
}
