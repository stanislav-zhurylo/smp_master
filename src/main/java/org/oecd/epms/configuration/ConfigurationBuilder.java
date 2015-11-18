package org.oecd.epms.configuration;

import java.util.Map;
import java.util.Properties;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class ConfigurationBuilder {
    
    public final static String PROPERTY_EMAIL_FROM = "emailFrom";
    public final static String PROPERTY_USERNAME = "username";
    public final static String PROPERTY_PASSWORD = "password";
    
    private JsonObject configuration = new JsonObject();

    public JsonObject getConfiguration() {
        return configuration;
    }
     
    public void setConfiguration(JsonObject configuration) {
        this.configuration = configuration;
    }
  
    public ConfigurationBuilder withParam(String key, String value){
        configuration.put(key, value);
        return this;
    }
    
    public ConfigurationBuilder withEmailFrom(String emailFrom){
        configuration.put(PROPERTY_EMAIL_FROM, emailFrom);
        return this;
    }
    
    public ConfigurationBuilder withUsername(String username){
        configuration.put(PROPERTY_USERNAME, username);
        return this;
    }
    
    public ConfigurationBuilder withPassword(String username){
        configuration.put(PROPERTY_PASSWORD, username);
        return this;
    }
    
    public DeploymentOptions toDeploymentOptions() {
        return new DeploymentOptions().setConfig(configuration); 
    } 
    
    public static Properties toProperties(JsonObject configuration){
        if(configuration == null || configuration.getMap() == null) {
            return null;
        } 
        Properties properties = new Properties();
        for(Map.Entry<String, Object> property : configuration.getMap().entrySet()) {
            properties.put(property.getKey(), property.getValue());
        }
        return properties;
    }
}
