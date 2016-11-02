package eu.crg.ega.sessionservice.model;

import eu.crg.ega.microservice.dto.auth.Session;
import eu.crg.ega.microservice.dto.auth.User;
import eu.crg.ega.microservice.enums.SessionStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Persistent
@Document
public class SessionUserModel {

  @Id
  private String id;

  private String userId;

  private SessionStatus status;

  private User user;

  private Session session;

}
