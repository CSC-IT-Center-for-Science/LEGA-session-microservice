package eu.crg.ega.sessionservice.security;

import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.security.RestWebAuthenticationDetails;
import eu.crg.ega.microservice.service.HttpConversionService;
import eu.crg.ega.sessionservice.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionCustomSimpleUrlLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler{

  @Autowired
  private SessionService sessionService;

  @Autowired
  private HttpConversionService httpConversionService;

  public SessionCustomSimpleUrlLogoutSuccessHandler() {
    this.setDefaultTargetUrl("/");
  }

  public SessionCustomSimpleUrlLogoutSuccessHandler(String defaultTargetURL) {
    this.setDefaultTargetUrl(defaultTargetURL);
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
                                                                                                                       IOException,
                                                                                                                              ServletException {
    if (authentication != null && authentication.isAuthenticated()) {
      Object details = authentication.getDetails();
      if (details != null && details instanceof RestWebAuthenticationDetails) {
        String token = ((RestWebAuthenticationDetails) details).getToken();
        Base<SessionUser> sessionUser = new Base<SessionUser>(sessionService.deleteSession(token));
        httpConversionService.convert(sessionUser,request,response);
      }
    }
    super.onLogoutSuccess(request, response, authentication);
  }
}