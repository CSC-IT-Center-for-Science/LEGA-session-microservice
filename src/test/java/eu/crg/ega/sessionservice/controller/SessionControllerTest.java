package eu.crg.ega.sessionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.crg.ega.microservice.dto.Base;
import eu.crg.ega.microservice.enums.LoginType;
import eu.crg.ega.microservice.test.util.TestUtils;
import eu.crg.ega.sessionservice.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SessionControllerTest {

  @Autowired
  private SessionController controller;

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {

    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
        .alwaysExpect(content().contentType(TestUtils.APPLICATION_JSON_CHARSET_UTF_8))
        .build();

  }

  @Test
  public void obtainTypes() throws Exception {
    MvcResult
        mvcResult =
        mockMvc.perform(get("/sessions/login/types")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    Base<LoginType> result = TestUtils.jsonToObject(mvcResult, LoginType.class, objectMapper);

    assertThat(result.getResponse().getNumTotalResults(), equalTo(Arrays.asList(LoginType.values()).size()));
  }

}
