package eu.crg.ega.sessionservice.repository.mongo;

import eu.crg.ega.microservice.repository.CustomQuerydslMongoRepository;
import eu.crg.ega.sessionservice.model.SessionUserModel;
import eu.crg.ega.sessionservice.repository.SessionUserRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("mongo")
@Repository
public interface MongoSessionUserRepository extends
    SessionUserRepository, CustomQuerydslMongoRepository<SessionUserModel, String> {

}
