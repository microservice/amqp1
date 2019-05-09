package io.storyscript.omg.services.amqp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * User: Jude Pereira
 * Date: 2019-04-27
 * Time: 23:21
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
