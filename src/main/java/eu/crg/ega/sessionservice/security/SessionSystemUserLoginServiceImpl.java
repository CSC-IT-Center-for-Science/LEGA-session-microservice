package eu.crg.ega.sessionservice.security;

import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.dto.auth.User;
import eu.crg.ega.microservice.enums.LoginType;
import eu.crg.ega.microservice.security.LoginTypeUsernamePasswordAuthenticationToken;
import eu.crg.ega.microservice.security.MySessionToken;
import eu.crg.ega.microservice.security.SystemUserLoginServiceImpl;
import eu.crg.ega.sessionservice.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.extern.log4j.Log4j;

@Log4j
@Service("sessionSystemUserLoginServiceImpl")
public class SessionSystemUserLoginServiceImpl extends SystemUserLoginServiceImpl {

  @Autowired
  private MySessionToken mySessionToken;

  @Autowired
  private SessionService sessionService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  protected void authenticateWithUsernameAndPassword() {
    LoginTypeUsernamePasswordAuthenticationToken loginTypeUsernamePasswordAuthenticationToken =
        new LoginTypeUsernamePasswordAuthenticationToken(username, password, LoginType.INTERNAL,
            null);
    Authentication authentication =
        authenticationManager.authenticate(loginTypeUsernamePasswordAuthenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    loginTypeUsernamePasswordAuthenticationToken =
        (LoginTypeUsernamePasswordAuthenticationToken) authentication;

    String hostAddress = null;
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      log.error("Exception retrieving host IP address", e);
    }
    SessionUser sessionUser =
        sessionService.createSession(
            (User) loginTypeUsernamePasswordAuthenticationToken.getPrincipal(), hostAddress);
    mySessionToken.setSessionToken(sessionUser.getSession().getSessionToken());
  }

}
