package org.oecd.epms.transfer;

import java.io.Serializable;

public class MessageDTS implements Serializable {
    private static final long serialVersionUID = 1L; 
     
    private String recipientEmail;
     
    private String subject;
     
    private String body;
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
}
