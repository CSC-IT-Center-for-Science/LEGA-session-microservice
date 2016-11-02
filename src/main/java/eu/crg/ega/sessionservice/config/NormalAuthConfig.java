package eu.crg.ega.sessionservice.config;

import eu.crg.ega.sessionservice.security.SessionAuthenticationProvider;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;

@Profile("!fakeauth")
@Configuration
public class NormalAuthConfig {

  @Bean
  @Qualifier("sessionAuthProvider")
  public AuthenticationProvider authProvider() {
    return new SessionAuthenticationProvider();
  }
}
