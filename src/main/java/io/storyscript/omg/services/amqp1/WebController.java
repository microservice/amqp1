package io.storyscript.omg.services.amqp1;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.BasicDBObject;
import io.storyscript.omg.services.amqp1.entities.PublishTextPayload;
import io.storyscript.omg.services.amqp1.entities.SubscribePayload;
import org.apache.qpid.amqp_1_0.jms.impl.BytesMessageImpl;
import org.apache.qpid.amqp_1_0.jms.impl.DestinationImpl;
import org.apache.qpid.amqp_1_0.jms.impl.TextMessageImpl;
import org.apache.qpid.amqp_1_0.type.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * User: Jude Pereira
 * Date: 2019-04-27
 * Time: 23:22
 */
@RestController
public class WebController {
    private static Logger logger = LoggerFactory.getLogger(WebController.class);

    private final Connection connection;

    public WebController() throws NamingException, JMSException {
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
        final String url = System.getenv("AMQP_URL");
        properties.put("connectionfactory.qpidConnectionfactory", url);

        Context context = new InitialContext(properties);

        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionfactory");
        this.connection = connectionFactory.createConnection();
        this.connection.start();
        logger.info("Connection opened to {}", url);
    }

    @RequestMapping(value = "/publish/text",
            method = RequestMethod.POST,
            consumes = "application/json")
    public String publishText(@RequestBody PublishTextPayload payload) throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        final DestinationImpl destination = DestinationImpl.valueOf(payload.getExchange());

        MessageProducer messageProducer = session.createProducer(destination);

        final BytesMessageImpl bm = (BytesMessageImpl) session.createBytesMessage();

        if (payload.getContent() != null) {
            bm.writeBytes(payload.getContent().getBytes(StandardCharsets.UTF_8));
            if (payload.getContentType() != null) {
                bm.setContentType(Symbol.getSymbol(payload.getContentType()));
            }
        }

        if (payload.getProperties() != null) {
            for (Map.Entry<String, Object> entry : payload.getProperties().entrySet()) {
                //noinspection unchecked
                bm.getApplicationProperties().getValue().put(entry.getKey(), entry.getValue());
            }
        }

        messageProducer.send(bm);
        session.close();
        return "ok\n";
    }

    private static String string(Object o) {
        if (o != null) return o.toString();

        return null;
    }

    @RequestMapping(
            value = "/subscribe/exchange/text",
            consumes = "application/json",
            method = RequestMethod.POST)
    public String subscribeToExchange(@RequestBody SubscribePayload payload) throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        final DestinationImpl destination = DestinationImpl.valueOf(payload.getExchange());

        MessageConsumer messageConsumer = session.createConsumer(destination);
        messageConsumer.setMessageListener(rawMessage -> {
            if (!(rawMessage instanceof TextMessageImpl)) {
                logger.error("Dropped incoming message - not an instance of TextMessageImpl, type={}",
                        rawMessage.getClass());
                return;
            }
            final TextMessageImpl message = (TextMessageImpl) rawMessage;
            final BasicDBObject data;
            try {
                data = new BasicDBObject()
                        .append("content_type", string(message.getContentType()))
                        .append("properties", message.getApplicationProperties().getValue())
                        .append("text", message.getText());
            } catch (JMSException e) {
                logger.error("Failed to build CE payload. Message dropped!", e);
                return;
            }

            BasicDBObject ce = new BasicDBObject()
                    .append("eventType", "trigger")
                    .append("cloudEventsVersion", "0.1")
                    .append("source", payload.getExchange())
                    .append("eventID", UUID.randomUUID().toString())
                    .append("eventTime", new Date().toString())
                    .append("contentType", "application/vnd.omg.object+json")
                    .append("data", data);

            logger.info("Dispatching {}", ce);

            Unirest.post(payload.getEndpoint())
                    .header("Content-Type", "application/json; charset=utf-8")
                    .body(ce.toJson()).asStringAsync(new Callback<>() {
                @Override
                public void completed(HttpResponse<String> httpResponse) {
                    // Do nothing.
                }

                @Override
                public void failed(UnirestException e) {
                    logger.error("Failed to deliver message", e);
                }

                @Override
                public void cancelled() {
                    // Will not happen.
                }
            });
        });
        return "ok\n";
    }
}
