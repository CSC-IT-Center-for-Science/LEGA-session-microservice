package eu.crg.ega.sessionservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.map.repository.config.EnableMapRepositories;

@Profile("mem")
@Configuration
@EnableMapRepositories(basePackages = "eu.crg.ega.sessionservice.repository.memory")
public class MemoryConfig {

}
