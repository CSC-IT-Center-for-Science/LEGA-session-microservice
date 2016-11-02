package eu.crg.ega.sessionservice.service;

import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.dto.auth.User;
import eu.crg.ega.microservice.enums.SessionStatus;
import eu.crg.ega.microservice.helper.CommonQuery;

import org.springframework.security.access.prepost.PreAuthorize;


public interface SessionService {

  //  @PreAuthorize("permitAll") //This gets called by a filter, so the auth has not yet being populated
  SessionUser checkSession(String token);

  @PreAuthorize("permitAll")
  SessionUser deleteSession(String tokenId);

  @PreAuthorize("permitAll")
  SessionUser createSession(User user, String ipAddress);

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  Base<SessionUser> listSessions(SessionStatus statusFilter, CommonQuery parseQuery);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SYSTEM')")
  String expireSessions();

}
