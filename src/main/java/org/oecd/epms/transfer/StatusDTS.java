package org.oecd.epms.transfer;

import java.io.Serializable;

public class StatusDTS implements Serializable {
    private static final long serialVersionUID = 1L;
     
    public enum DispatchStatus { SUCCESS, ERROR };
    
    private DispatchStatus status;
     
    private String message;
    
    private String stackTrace;

    public DispatchStatus getStatus() {
        return status;
    }

    public void setStatus(DispatchStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    } 
    
    public StatusDTS withDispatchStatus(DispatchStatus status) {
        this.status = status;
        return this;
    }
    
    public StatusDTS withMessage(String message) {
        this.message = message;
        return this;
    }
    
    public StatusDTS withStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }
}
