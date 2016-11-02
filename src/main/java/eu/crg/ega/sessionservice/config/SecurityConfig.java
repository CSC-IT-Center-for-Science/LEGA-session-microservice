package eu.crg.ega.sessionservice.config;

import eu.crg.ega.microservice.constant.ParamName;
import eu.crg.ega.microservice.filter.LoginTypeUsernamePasswordAuthenticationTokenFilter;
import eu.crg.ega.microservice.filter.RestTokenPreAuthenticatedProcessingFilter;
import eu.crg.ega.microservice.helper.CustomRequestMatcher;
import eu.crg.ega.microservice.security.CustomSimpleUrlFailureHandler;
import eu.crg.ega.microservice.security.RestAuthenticationEntryPoint;
import eu.crg.ega.microservice.security.RestWebAuthenticationDetailsSource;
import eu.crg.ega.sessionservice.security.MySavedRequestAwareAuthenticationSuccessHandler;
import eu.crg.ega.sessionservice.security.SessionCustomSimpleUrlLogoutSuccessHandler;
import eu.crg.ega.sessionservice.security.SessionRestTokenAuthenticationUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  Environment environment;

  //LOGIN "FORM" for rest authentication provider and filter
  @Bean
  public SimpleUrlAuthenticationFailureHandler failureHandler() {
    return new CustomSimpleUrlFailureHandler();
  }

  @Bean
  public LoginTypeUsernamePasswordAuthenticationTokenFilter loginTypeUsernamePasswordAuthenticationTokenFilter() {
    return new LoginTypeUsernamePasswordAuthenticationTokenFilter(mySavedRequestAwareAuthenticationSuccessHandler());
  }

  @Bean
  public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
    return new RestAuthenticationEntryPoint();
  }

  @Bean
  public MySavedRequestAwareAuthenticationSuccessHandler mySavedRequestAwareAuthenticationSuccessHandler() {
    return new MySavedRequestAwareAuthenticationSuccessHandler();
  }

  @Autowired
  @Qualifier("sessionAuthProvider")
  public AuthenticationProvider sessionAuthenticationProvider;
  //END LOGIN "FORM" for rest

  //REST token authentication Provider and filter
  @Bean
  public SessionRestTokenAuthenticationUserDetailsService sessionRestTokenAuthenticationUserDetailsService() {
    return new SessionRestTokenAuthenticationUserDetailsService();
  }

  @Bean
  public AuthenticationProvider restTokenAuthenticationProvider() {
    PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
    provider.setPreAuthenticatedUserDetailsService(sessionRestTokenAuthenticationUserDetailsService());
    return provider;
  }

  @Bean
  public AuthenticationDetailsSource restWebAuthenticationDetailsSource() {
    return new RestWebAuthenticationDetailsSource();
  }

  @Bean
  public RestTokenPreAuthenticatedProcessingFilter restTokenPreAuthenticatedProcessingFilter(
      final AuthenticationManager authenticationManager) {
    RestTokenPreAuthenticatedProcessingFilter filter = new RestTokenPreAuthenticatedProcessingFilter();
    filter.setAuthenticationManager(authenticationManager);
    filter.setInvalidateSessionOnPrincipalChange(true);
    filter.setCheckForPrincipalChanges(false);
    filter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
    filter.setAuthenticationDetailsSource(restWebAuthenticationDetailsSource());
    return filter;
  }
  //END REST token authentication Provider

  //LOGOUT
  @Bean
  public SessionCustomSimpleUrlLogoutSuccessHandler sessionCustomSimpleLogoutUrlSuccessHandler() {
    return new SessionCustomSimpleUrlLogoutSuccessHandler("/");
  }

  @Bean
  public SecurityContextLogoutHandler securityContextLogoutHandler() {
    return new SecurityContextLogoutHandler();
  }

  @Bean
  public RequestMatcher customLogoutRequestMatcher() {
    return new CustomRequestMatcher(Arrays.asList(ParamName.LOGOUT_FULL_ENDPOINT), HttpMethod.DELETE);
  }

  @Bean
  public LogoutFilter logoutFilter() {
    LogoutFilter logoutFilter = new LogoutFilter(sessionCustomSimpleLogoutUrlSuccessHandler(), securityContextLogoutHandler());
    logoutFilter.setLogoutRequestMatcher(customLogoutRequestMatcher());
    return logoutFilter;
  }
  // END LOGOUT

  //Access Authentication Manager Bean
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
  // END Access Authentication Manager Bean

  //CONFIGURATION
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //Add auth provider for session
    auth.authenticationProvider(sessionAuthenticationProvider);
    //Add auth provider for token
    auth.authenticationProvider(restTokenAuthenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    String apiVersion = environment.getProperty("server.servlet-path");

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Do not create sessions

    http.authorizeRequests()
        .antMatchers(apiVersion + "/info").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/metrics/**").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/dump").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/trace").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/mappings").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/config/**").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/autoconfig").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/beans").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/health").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/configprops").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/logs/**").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers(apiVersion + "/login").permitAll();

    http.formLogin().disable();

    http.csrf().disable();

    http.logout().disable();

    http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint());

    http.addFilterBefore(restTokenPreAuthenticatedProcessingFilter(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class);

    http.addFilterBefore(loginTypeUsernamePasswordAuthenticationTokenFilter(),
        UsernamePasswordAuthenticationFilter.class);

    http.addFilterAfter(logoutFilter(), RestTokenPreAuthenticatedProcessingFilter.class);
  }
  //END CONFIGURATION
}