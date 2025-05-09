package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;


@ExcludeFromJacocoGeneratedReport
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
