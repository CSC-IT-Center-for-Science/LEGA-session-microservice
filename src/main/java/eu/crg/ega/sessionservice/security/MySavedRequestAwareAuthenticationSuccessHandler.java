package eu.crg.ega.sessionservice.security;

import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.dto.auth.User;
import eu.crg.ega.microservice.service.HttpConversionService;
import eu.crg.ega.microservice.util.NetUtils;
import eu.crg.ega.sessionservice.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MySavedRequestAwareAuthenticationSuccessHandler extends
    SimpleUrlAuthenticationSuccessHandler {

  @Autowired
  private SessionService sessionService;

  @Autowired
  private HttpConversionService httpConversionService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {

    Object principal = authentication.getPrincipal();
    SessionUser sessionUser =
        sessionService.createSession((User) principal, NetUtils.getClientIpAddress(request));
    Base<SessionUser> returnObject = new Base<SessionUser>(sessionUser);
    httpConversionService.convert(returnObject, request, response);
  }

}