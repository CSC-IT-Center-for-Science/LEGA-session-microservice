package eu.crg.ega.sessionservice.security;

import eu.crg.ega.microservice.dto.auth.User;
import eu.crg.ega.microservice.enums.LoginType;
import eu.crg.ega.microservice.rest.RestSender;
import eu.crg.ega.microservice.security.LoginTypeUsernamePasswordAuthenticationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;

public class SessionFakeAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private RestSender restSender;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    LoginType loginType =
        ((LoginTypeUsernamePasswordAuthenticationToken) authentication).getLoginType();

    try {
      HashSet<GrantedAuthority> givenAuthorities = new HashSet<>();
      givenAuthorities.add(new SimpleGrantedAuthority("ROLE_SYSTEM"));
      givenAuthorities.add(new SimpleGrantedAuthority("ROLE_SYSTEM_BASIC"));
      givenAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
      givenAuthorities.add(new SimpleGrantedAuthority("ROLE_REQUESTER"));
      givenAuthorities.add(new SimpleGrantedAuthority("ROLE_SUBMITTER"));
      givenAuthorities.add(new SimpleGrantedAuthority("ROLE_FTP"));
      givenAuthorities.add(new SimpleGrantedAuthority("ROLE_INTERNAL"));

      User returnUser = User
          .builder()
          //.userId(java.util.UUID.randomUUID().toString())
          .userId(authentication.getPrincipal().toString().concat("_userid"))
          .username(authentication.getPrincipal().toString())
          .accountNonLocked(true)
          .accountNonExpired(true)
          .credentialsNonExpired(true)
          .enabled(true)
          .authorities(givenAuthorities)
          .build();

      LoginTypeUsernamePasswordAuthenticationToken loginTypeUsernamePasswordAuthenticationToken =
          new LoginTypeUsernamePasswordAuthenticationToken(returnUser, authentication
              .getCredentials().toString(), loginType, returnUser.getAuthorities(), null);

      return loginTypeUsernamePasswordAuthenticationToken;

    } catch (Exception ex) {
      throw new AuthenticationServiceException("Authentication failed", ex);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return LoginTypeUsernamePasswordAuthenticationToken.class.equals(authentication);
  }

}
