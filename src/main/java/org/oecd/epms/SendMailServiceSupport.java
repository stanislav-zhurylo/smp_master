package org.oecd.epms;

import static org.oecd.epms.configuration.ConfigurationBuilder.PROPERTY_EMAIL_FROM;
import static org.oecd.epms.configuration.ConfigurationBuilder.PROPERTY_PASSWORD;
import static org.oecd.epms.configuration.ConfigurationBuilder.PROPERTY_USERNAME;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.oecd.epms.transfer.MessageDTS;
import org.oecd.epms.validation.ValidationSupport;

public class SendMailServiceSupport {  
	private static final Logger log = LoggerFactory.getLogger(SendMailServiceSupport.class);
	
    private Properties configuration;  
    private ValidationSupport validationSupport;  
    
    public SendMailServiceSupport(Properties configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("You have to supply SMTP connection params to deploy verticle");
        }
        this.configuration = configuration;
        this.validationSupport = new ValidationSupport();
    }
    
    public void validateAndSendMessage(MessageDTS messageDts) throws Exception {
        validationSupport.validateMessage(messageDts);
        Session session = Session.getInstance(configuration, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        configuration.getProperty(PROPERTY_USERNAME), 
                        configuration.getProperty(PROPERTY_PASSWORD));
            }
        });
        Transport.send(prepareMimeMessage(session, 
                configuration.getProperty(PROPERTY_EMAIL_FROM), 
                messageDts));  
    	log.info("Email has been sent successfuly");
    }
     
    private Message prepareMimeMessage(Session session, String emailFrom, MessageDTS messageDts)
            throws AddressException, MessagingException {
        Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(emailFrom));
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(messageDts.getRecipientEmail()));
        mimeMessage.setSubject(messageDts.getSubject());
        mimeMessage.setText(messageDts.getBody());
        return mimeMessage;
    } 
}
