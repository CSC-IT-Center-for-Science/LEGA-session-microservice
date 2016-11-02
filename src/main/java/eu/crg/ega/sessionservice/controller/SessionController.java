package eu.crg.ega.sessionservice.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import eu.crg.ega.microservice.constant.ParamName;
import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.enums.LoginType;
import eu.crg.ega.microservice.enums.SessionStatus;
import eu.crg.ega.microservice.helper.CommonQueryHelper;
import eu.crg.ega.sessionservice.service.SessionService;
import eu.crg.ega.swaggerconstants.session.SwaggerSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@Api(value = "sessions", description = SwaggerSession.DESCRIPTION, position = 7)
@RequestMapping(value = "/sessions")
@RestController
public class SessionController {
  
  @Autowired
  private SessionService sessionService;

  @ApiOperation(value = SwaggerSession.LOGIN_TYPES, notes = SwaggerSession.LOGIN_TYPES_NOTES)
  @RequestMapping(value = "/login/types", method = RequestMethod.GET)
  public Base<LoginType> listLoginTypes() {
    return new Base<>(Arrays.asList(LoginType.values()));
  }
  
  @ApiOperation(value = SwaggerSession.CHECK_SESSION_ID, notes = SwaggerSession.CHECK_SESSION_ID_NOTES)
  @RequestMapping(value = "/{tokenid}", method = RequestMethod.GET)
  public Base<SessionUser> sessionCheckValue(
      @ApiParam(name = "tokenId", value = SwaggerSession.CHECK_SESSION_ID_SESSION_ID, required = true)
      @PathVariable(value="tokenid") String tokenId) {

    return new Base<>(sessionService.checkSession(tokenId));
  }

  @ApiOperation(value = SwaggerSession.LOGOUT, notes = SwaggerSession.LOGOUT_NOTES)
  @RequestMapping(value = "/{tokenid}", method = RequestMethod.DELETE)
  public Base<SessionUser> tokenLogout(
      @ApiParam(name = "tokenId", value = SwaggerSession.LOGOUT_SESSION_ID, required = true)
      @PathVariable(value = "tokenid") String tokenId) {

    return new Base<>(sessionService.deleteSession(tokenId));
  }
  
  @PreAuthorize("permitAll")
  @ApiOperation(value = SwaggerSession.LIST_SESSIONS_STATUS, notes = SwaggerSession.LIST_SESSIONS_STATUS_NOTES)
  @RequestMapping(value = "/status", method = RequestMethod.GET)
  public Base<SessionStatus> listSessionStatus() {
    return new Base<>(Arrays.asList(SessionStatus.values()));
  }
  
  @ApiOperation(value = SwaggerSession.LIST_SESSIONS, notes = SwaggerSession.LIST_SESSIONS_NOTES)
  @RequestMapping(value = "", method = RequestMethod.GET)
  public Base<SessionUser> listSessions(
      @ApiParam(name = ParamName.STATUS, value = SwaggerSession.LIST_SESSIONS_STATUS, required = false)
      @RequestParam(value = ParamName.STATUS, required = false, defaultValue = "active") String sessionStatus,
      @ApiParam(name = ParamName.PARAMS, value = SwaggerSession.LIST_SESSIONS_PARAMS, required = false)
      @RequestParam(required = false) Map<String, String> params) {

    SessionStatus statusFilter = SessionStatus.parse(sessionStatus);
    return sessionService.listSessions(statusFilter, CommonQueryHelper.parseQuery(params));
  }
  
  @ApiOperation(value = SwaggerSession.EXPIRE_SESSIONS, notes = SwaggerSession.EXPIRE_SESSIONS_NOTES)
  @RequestMapping(value = "", method = RequestMethod.PUT)
  public Base<String> expireSessions() {
    return new Base<String>(sessionService.expireSessions());
  }
  
}
