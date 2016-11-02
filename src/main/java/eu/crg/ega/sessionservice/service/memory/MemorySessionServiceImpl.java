package eu.crg.ega.sessionservice.service.memory;

import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.enums.SessionStatus;
import eu.crg.ega.microservice.exception.NotImplementedException;
import eu.crg.ega.microservice.helper.CommonQuery;
import eu.crg.ega.sessionservice.service.SessionServiceImpl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Profile("mem")
@Service
@Log4j
public class MemorySessionServiceImpl extends SessionServiceImpl {

  @Override
  public Base<SessionUser> listSessions(SessionStatus statusFilter, CommonQuery parseQuery) {
    throw new NotImplementedException("Method not implemented for this repository");
  }

  @Override
  public String expireSessions() {
    throw new NotImplementedException("Method not implemented for this repository");
  }
}
