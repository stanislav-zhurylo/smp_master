package org.oecd.epms.validation;

import java.util.List;
  
public class ValidationException extends Exception {
    static final long serialVersionUID = 7818375828146090155L;
    
    public ValidationException(List<String> constraintViolations) {
        super(retrieveViolationsAsString(constraintViolations));
    }
     
    private static String retrieveViolationsAsString(List<String> constraintViolations) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String violation : constraintViolations) {
            stringBuilder.append(violation + ". ");
        }
        return stringBuilder.toString();
    }
}
