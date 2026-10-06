package com.casbin.shiro.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CasdoorSpringBootShiroExampleApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void indexIsPublic() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login with Casdoor")));
    }

    @Test
    void foosNeedASignedInUser() throws Exception {
        mvc.perform(get("/foos")).andExpect(redirectedUrl("/login"));
    }

    @Test
    void loginRedirectsToCasdoorWithAState() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("https://door.casdoor.com/login/oauth/authorize?")))
                .andExpect(header().string("Location", containsString("&state=")));
    }

    @Test
    void callbackRejectsAnInvalidState() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mvc.perform(get("/login").session(session));
        mvc.perform(get("/login/oauth2").param("code", "code").param("state", "forged").session(session))
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", startsWith("Invalid state")));
    }
}
