package eu.crg.ega.sessionservice.repository;

import eu.crg.ega.microservice.enums.SessionStatus;
import eu.crg.ega.sessionservice.model.SessionUserModel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SessionUserRepository extends CrudRepository<SessionUserModel, String> {

  public SessionUserModel findByUserIdAndStatus(String userId, SessionStatus status);

}
