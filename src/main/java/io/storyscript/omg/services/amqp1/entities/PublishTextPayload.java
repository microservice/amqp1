package io.storyscript.omg.services.amqp1.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * User: Jude Pereira
 * Date: 2019-04-30
 * Time: 11:45
 */
public class PublishTextPayload {
    private String exchange;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    private String content;

    @JsonProperty("content_type")
    private String contentType;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getExchange() {
        return exchange;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        if (contentType == null) {
            return "text/plain";
        }
        return contentType;
    }
}
