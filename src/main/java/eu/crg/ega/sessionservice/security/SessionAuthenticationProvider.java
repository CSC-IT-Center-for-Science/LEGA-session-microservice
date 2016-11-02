package eu.crg.ega.sessionservice.security;

import eu.crg.ega.microservice.constant.ParamName;
import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.dto.auth.User;
import eu.crg.ega.microservice.enums.LoginType;
import eu.crg.ega.microservice.enums.ServiceType;
import eu.crg.ega.microservice.rest.RestSender;
import eu.crg.ega.microservice.security.LoginTypeUsernamePasswordAuthenticationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SessionAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private RestSender restSender;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    LoginType loginType =
        ((LoginTypeUsernamePasswordAuthenticationToken) authentication).getLoginType();

    String password = authentication.getCredentials().toString();

    try {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
      params.add(ParamName.USERNAME, authentication.getPrincipal().toString());
      params.add(ParamName.PASSWORD, new String(password));
      params.add(ParamName.LOGIN_TYPE, loginType.getValue());

      final Base<User> returnedUser =
          restSender.sendMicroservice(ServiceType.AUTH, ParamName.LOGIN_ENDPOINT,
              HttpMethod.POST, params, null, null, User.class,
              MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON);

      if (returnedUser == null || returnedUser.getHeader() == null
          || !"200".equals(returnedUser.getHeader().getCode())
          || returnedUser.getResponse() == null || returnedUser.getResponse().getResult() == null
          || returnedUser.getResponse().getResult().size() != 1) {
        throw new AuthenticationServiceException("Authentication Failed");
      }

      final User principalFromRest = returnedUser.getResponse().getResult().get(0);

      LoginTypeUsernamePasswordAuthenticationToken loginTypeUsernamePasswordAuthenticationToken =
          new LoginTypeUsernamePasswordAuthenticationToken(principalFromRest, authentication
              .getCredentials().toString(), loginType, principalFromRest.getAuthorities(), null);

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
