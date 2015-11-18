package org.oecd.epms.validation;

import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.oecd.epms.transfer.MessageDTS;
import com.google.common.collect.Lists;

public class ValidationSupport {
    
    private static final String EMAIL_PATTERN = 
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    public final static String MSG_EMAIL_CANT_BE_EMPTY = "Recipient's email can't be empty";
    public final static String MSG_EMAIL_DOESNT_MATCH_PATTERN = "Recipient's email doesnt meet pattern requirements";
    public final static String MSG_SUBJECT_CANT_BE_EMPTY = "Subject can't be empty";
    public final static String MSG_SUBJECT_SHOULDNT_EXCEED_128_CHARS = "Subject shouldn't exceed the length of 128 chars";
    public final static String MSG_MESSAGE_BODY_CANT_BE_EMPTY = "Message body can't be empty";
    
    public void validateMessage(MessageDTS messageDts) throws ValidationException {
        List<String> constraintViolations = Lists.newArrayList();
        
        //validate recipients email 
        if(StringUtils.isEmpty(messageDts.getRecipientEmail())) {
            constraintViolations.add(MSG_EMAIL_CANT_BE_EMPTY);
        } else {
            if(!Pattern.compile(EMAIL_PATTERN).matcher(messageDts.getRecipientEmail()).matches()) {
                constraintViolations.add(MSG_EMAIL_DOESNT_MATCH_PATTERN);
            }
        }
        
        //validate subject
        if(StringUtils.isEmpty(messageDts.getSubject())) {
            constraintViolations.add(MSG_SUBJECT_CANT_BE_EMPTY);
        } else {
            if(messageDts.getSubject().length() > 128) {
                constraintViolations.add(MSG_SUBJECT_SHOULDNT_EXCEED_128_CHARS);
            }
        }
        
        //validate message body 
        if(StringUtils.isEmpty(messageDts.getBody())) {
            constraintViolations.add(MSG_MESSAGE_BODY_CANT_BE_EMPTY);
        }
        
        if(constraintViolations.size() > 0) {
            throw new ValidationException(constraintViolations);
        }
    }
    
}
