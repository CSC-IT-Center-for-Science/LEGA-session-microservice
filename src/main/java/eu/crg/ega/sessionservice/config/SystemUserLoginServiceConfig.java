package eu.crg.ega.sessionservice.config;

import eu.crg.ega.microservice.security.SystemUserLoginService;
import eu.crg.ega.sessionservice.security.SessionSystemUserLoginServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemUserLoginServiceConfig {

  /**
   * Injects this specific implementation for this interface.
   * 
   * @return
   */
  @Bean(name = "systemUserLoginService")
  public SystemUserLoginService systemUserLoginService() {
    return new SessionSystemUserLoginServiceImpl();
  }
  
}
