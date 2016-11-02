package eu.crg.ega.sessionservice.service.mongo;

import com.mysema.query.types.expr.BooleanExpression;

import eu.crg.ega.microservice.constant.CoreConstants;
import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.dto.auth.SessionUser;
import eu.crg.ega.microservice.enums.SessionStatus;
import eu.crg.ega.microservice.helper.CommonQuery;
import eu.crg.ega.microservice.util.Converter;
import eu.crg.ega.sessionservice.model.QSessionUserModel;
import eu.crg.ega.sessionservice.model.SessionUserModel;
import eu.crg.ega.sessionservice.repository.mongo.MongoSessionUserRepository;
import eu.crg.ega.sessionservice.service.SessionServiceImpl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.log4j.Log4j;

@Profile("mongo")
@Service
@Log4j
public class MongoSessionServiceImpl extends SessionServiceImpl {

  @Override
  protected DateTime getLatestLogin(String userId) {
    DateTime latestLogin = null;
    QSessionUserModel qSessionUser = QSessionUserModel.sessionUserModel;
    BooleanExpression condition = qSessionUser.userId.eq(userId);
    Pageable pageable = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "session.expeditionTime"));
    Page<SessionUserModel> result = ((MongoSessionUserRepository) sessionUserRepository).findAll(condition, pageable);
    if (result.getTotalElements() > 0) {
      SessionUserModel sessionUser = result.getContent().get(0);
      latestLogin = sessionUser.getSession().getExpeditionTime();
    }
    return latestLogin;
  }

  @Override
  public Base<SessionUser> listSessions(SessionStatus status, CommonQuery commonQuery) {
    QSessionUserModel qSessionUser = QSessionUserModel.sessionUserModel;
    BooleanExpression withStatus = null;

    long numTotalResults = 0;
    List<SessionUser> sessionsList = null;
    switch (status) {
      case ACTIVE:
        withStatus = qSessionUser.status.eq(SessionStatus.ACTIVE);
        break;
      case EXPIRED:
        withStatus = qSessionUser.status.eq(SessionStatus.EXPIRED);
        break;
      case ALL:
        withStatus = qSessionUser.status.in(SessionStatus.ACTIVE, SessionStatus.EXPIRED);
        break;
    }
    numTotalResults = ((MongoSessionUserRepository) sessionUserRepository).count(withStatus);
    sessionsList =
        Converter.convertList(SessionUser.class,
            ((MongoSessionUserRepository) sessionUserRepository).findAll(withStatus, commonQuery).getContent());

    return new Base<>((int) numTotalResults, sessionsList);
  }

  public String expireSessions() {
    log.debug("Expiring sessions..");
    QSessionUserModel qSessionUser = QSessionUserModel.sessionUserModel;
    BooleanExpression statusActive = qSessionUser.status.eq(SessionStatus.ACTIVE);
    BooleanExpression notExpired = qSessionUser.session.expirationTime.before(DateTime.now().withZone(DateTimeZone.UTC));

    List<SessionUserModel> shouldBeExpired =
        (List<SessionUserModel>) ((MongoSessionUserRepository) sessionUserRepository).findAll(statusActive.and(notExpired));
    if (shouldBeExpired != null) {
      for (SessionUserModel sessionUser : shouldBeExpired) {
        log.trace("Expiring session: " + sessionUser.getSession() + " for user " + sessionUser.getUserId() + " with username" + sessionUser.getUser().getUsername());
        sessionUser.setStatus(SessionStatus.EXPIRED);
        sessionUserRepository.save(sessionUser);
      }
    }
    return CoreConstants.OK;
  }

}
