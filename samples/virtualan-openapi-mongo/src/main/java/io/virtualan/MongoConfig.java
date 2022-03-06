package io.virtualan;

import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import com.mongodb.MongoClient;

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