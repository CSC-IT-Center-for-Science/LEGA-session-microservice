package eu.crg.ega.sessionservice;

import eu.crg.ega.microservice.security.SystemUserLoginService;
import eu.crg.ega.sessionservice.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Profile("!mem")
@Log4j
@Component
public class OnAppContextStartup implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private SystemUserLoginService systemUserLoginService;

  @Autowired
  private SessionService sessionService;

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    boolean ok = false;
    try {
      systemUserLoginService.addSystemUserToContext();
      ok = true;

    } catch (Exception e) {
      log.error("Exception adding system user to context", e);
    }

    if (ok) {
      sessionService.expireSessions();
    }
  }

}
