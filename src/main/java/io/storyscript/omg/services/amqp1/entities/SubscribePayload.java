package io.storyscript.omg.services.amqp1.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * User: Jude Pereira
 * Date: 2019-04-29
 * Time: 16:46
 */
public class SubscribePayload {
    private String endpoint;

    @JsonProperty("data")
    private Map<String, String> data;

    public String getEndpoint() {
        return endpoint;
    }

    public String getExchange() {
        return data.get("name");
    }
}
