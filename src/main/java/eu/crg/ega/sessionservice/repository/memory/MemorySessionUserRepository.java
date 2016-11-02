package eu.crg.ega.sessionservice.repository.memory;

import eu.crg.ega.sessionservice.repository.SessionUserRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("mem")
@Repository
public interface MemorySessionUserRepository extends SessionUserRepository {
}
