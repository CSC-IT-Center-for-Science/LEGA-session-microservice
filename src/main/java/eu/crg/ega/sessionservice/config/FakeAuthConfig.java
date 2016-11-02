package eu.crg.ega.sessionservice.config;

import eu.crg.ega.sessionservice.security.SessionFakeAuthenticationProvider;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;

@Profile("fakeauth")
@Configuration
public class FakeAuthConfig {

  @Bean
  @Qualifier("sessionAuthProvider")
  public AuthenticationProvider authProvider() {
    return new SessionFakeAuthenticationProvider();
  }
}

