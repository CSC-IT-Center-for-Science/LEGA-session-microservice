package eu.crg.ega.sessionservice.task;

import eu.crg.ega.microservice.security.SystemUserLoginService;
import eu.crg.ega.sessionservice.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionExpiration {

  @Autowired
  SessionService sessionService;

  @Autowired
  SystemUserLoginService systemUserLoginService;

  @Scheduled(fixedDelay = 300000, initialDelay = 600000)
  public void expireSessions() {
    systemUserLoginService.addSystemUserToContext();
    sessionService.expireSessions();
  }
}
