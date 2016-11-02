package eu.crg.ega.sessionservice.security;

import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.sessionservice.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class SessionRestTokenAuthenticationUserDetailsService implements
                                                              AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  @Autowired
  SessionService sessionservice;

  @Override
  public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws
                                                                                UsernameNotFoundException {

    try {
      final SessionUser
          returnedSessionUser =
          sessionservice.checkSession(token.getPrincipal().toString());
      if (returnedSessionUser == null) {
        throw new UsernameNotFoundException("Username not found");
      }
      return returnedSessionUser.getUser();
    } catch (Exception e) {
      throw new UsernameNotFoundException(e.getLocalizedMessage());
    }
  }
}