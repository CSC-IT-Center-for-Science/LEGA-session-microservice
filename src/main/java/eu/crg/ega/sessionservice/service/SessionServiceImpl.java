package eu.crg.ega.sessionservice.service;

import eu.crg.ega.microservice.dto.auth.Session;
import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.dto.auth.User;
import eu.crg.ega.microservice.enums.SessionStatus;
import eu.crg.ega.microservice.exception.SessionAlreadyClosed;
import eu.crg.ega.microservice.exception.SessionInvalidException;
import eu.crg.ega.microservice.exception.SessionTimedOutException;
import eu.crg.ega.microservice.util.Converter;
import eu.crg.ega.sessionservice.model.SessionUserModel;
import eu.crg.ega.sessionservice.repository.SessionUserRepository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

import lombok.extern.log4j.Log4j;

@Log4j
public abstract class SessionServiceImpl implements SessionService {

  private static final String SESSION_ALREADY_CLOSED = "Session already closed";

  private static final String INVALID_SESSION = "Invalid session";

  private static final String SESSION_TIMED_OUT = "Session timed out";

  @Value("${session.duration}")
  protected Integer sessionDuration;

  @Autowired
  protected SessionUserRepository sessionUserRepository;

  private SessionUser isSessionValid(String sessionId) {
    // check cache if session is still alive

    SessionUserModel sessionUser = sessionUserRepository.findOne(sessionId);

    if (sessionUser == null) {
      throw new SessionInvalidException(INVALID_SESSION);
    }

    if (sessionUser.getStatus() == SessionStatus.EXPIRED
        || sessionUser.getSession().getExpirationTime().isBeforeNow()) {

      sessionUser.setStatus(SessionStatus.EXPIRED);
      sessionUserRepository.save(sessionUser);
      throw new SessionTimedOutException(SESSION_TIMED_OUT);
    }

    //If session is active and has not already expired, we renew the session
    sessionUser.getSession().setExpirationTime(DateTime.now().withZone(DateTimeZone.UTC).plusMinutes(sessionDuration));
    sessionUserRepository.save(sessionUser);

    return Converter.convert(SessionUser.class, sessionUser);
  }

  /**
   * Creates a new session for this user and stores it.
   */
  public SessionUser createSession(User user, String ipAddress) {
    log.debug("Entering createSession(" + user + ", " + ipAddress + ")");

    // Find latest login of this user (if any)
    DateTime latestLogin = getLatestLogin(user.getUserId());

    UUID uuid = UUID.randomUUID();
    String sessiontoken = uuid.toString();
    DateTime dateTime = DateTime.now().withZone(DateTimeZone.UTC);
    Session session = new Session(sessiontoken, dateTime, dateTime.plusMinutes(sessionDuration), "N/A", ipAddress, latestLogin);

    SessionUserModel sessionUser =
        SessionUserModel.builder().id(sessiontoken).status(SessionStatus.ACTIVE)
            .userId(user.getUserId()).user(user).session(session).build();
    sessionUserRepository.save(sessionUser);

    log.debug("Exiting createSession()");
    return Converter.convert(SessionUser.class, sessionUser);
  }

  protected DateTime getLatestLogin(String userId) {
    return null;
  }

  @Override
  public SessionUser checkSession(String token) {
    SessionUser userLogin = isSessionValid(token);
    return userLogin;
  }

  @Override
  public SessionUser deleteSession(String tokenId) {
    try {
      // Check session to close is still valid
      isSessionValid(tokenId);
    } catch (Exception e) {
      throw new SessionAlreadyClosed(SESSION_ALREADY_CLOSED);
    }

    SessionUserModel sessionUserModel = sessionUserRepository.findOne(tokenId);
    sessionUserModel.setStatus(SessionStatus.EXPIRED);
    sessionUserRepository.save(sessionUserModel);

    SessionUser sessionUser = Converter.convert(SessionUser.class, sessionUserModel);
    sessionUser.setSession(null);
    return sessionUser;
  }

}
