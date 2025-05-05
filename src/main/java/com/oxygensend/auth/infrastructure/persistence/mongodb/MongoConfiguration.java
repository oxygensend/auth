package com.oxygensend.auth.infrastructure.persistence.mongodb;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Profile("MONGODB")
@Configuration
public class MongoConfiguration {
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory, TransactionOptions.builder()
                                                                                   .writeConcern(WriteConcern.MAJORITY)
                                                                                   .readConcern(ReadConcern.MAJORITY)
                                                                                   .readPreference(
                                                                                       ReadPreference.primary())
                                                                                   .build());
    }
}
