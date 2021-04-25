package com.licentamihai.alumni.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.licentamihai.alumni.dto.BasicUserLogin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {UsersController.class})
@WebMvcTest
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    private final static String TEST_USER_ID = "student";
    private final static String TEST_USER_PASSWORD = "studentp";

    private static Logger logger = LogManager.getLogger(UserControllerTest.class);

//    @Autowired
//    private EmployeeRepository repository;

    @Test
    public void userValidateTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        final BasicUserLogin basicUserLogin = new BasicUserLogin(TEST_USER_ID, TEST_USER_PASSWORD);
        final String simpleUserLoginJson = objectMapper.writeValueAsString(basicUserLogin);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/user/validate")
                .content(simpleUserLoginJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "true");
    }
}
