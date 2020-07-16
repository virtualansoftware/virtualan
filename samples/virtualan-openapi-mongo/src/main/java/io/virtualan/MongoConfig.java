package io.virtualan;

import java.io.IOException;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.*;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.mongo.config.Net;


public class MongoConfig {
    private static final String MONGO_DB_URL = "localhost";

    static MongoClient mongo = mongoClient();
    public static MongoClient mongoClient() {
        if (mongo == null) {
            try {
                EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
                mongo.setBindIp(MONGO_DB_URL);
                return mongo.getObject();
            } catch (Exception e) {
                return null;
            }
        } else {
            return mongo;
        }
    }

}