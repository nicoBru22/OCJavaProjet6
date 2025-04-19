package com.projet6.payMyBuddy.controller;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	
	@Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
	void testUserList() throws Exception {
		String usernameTest = "nicolas";
		String emailTest = "email@test.fr";
		String passwordTest = "passwordTest";
		String roleTest = "USER";
        List<User> connectionsTest = new ArrayList<>();
		
        User userTest = new User();

        userTest.setId(1L);
        userTest.setUsername(usernameTest);
        userTest.setEmail(emailTest);
        userTest.setRole(roleTest);
        userTest.setPassword(passwordTest);
        userTest.setConnections(connectionsTest);
        
		List<User> userListTest = new ArrayList<>();
		userListTest.add(userTest);
		
		when(userService.getAllUser()).thenReturn(userListTest);
		
		mockMvc.perform(get("/users/list_user"))
		.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("nicolas"))  // Vérification du contenu
        .andExpect(jsonPath("$[0].email").value("email@test.fr"));
		
		
		verify(userService, times(1)).getAllUser();
	}
	
	@Test
    @WithMockUser(username = "testUser", roles = "USER")
	void testAddUser() throws Exception {
		String usernameTest = "nicolas";
		String emailTest = "email@test.fr";
		String passwordTest = "passwordTest";
		String roleTest = "USER";
        List<User> connectionsTest = new ArrayList<>();
		
        User userTest = new User();

        userTest.setId(1L);
        userTest.setUsername(usernameTest);
        userTest.setEmail(emailTest);
        userTest.setRole(roleTest);
        userTest.setPassword(passwordTest);
        userTest.setConnections(connectionsTest);
		
		when(userService.addUser(usernameTest, emailTest, passwordTest)).thenReturn(userTest);
		
		mockMvc.perform(post("/users/add_user")
		        .param("username", usernameTest)
		        .param("email", emailTest)
		        .param("password", passwordTest)
		        .with(csrf()))
			.andExpect(status().isFound())
			.andExpect(header().string("Location", "/login"));
		
		verify(userService, times(1)).addUser(usernameTest, emailTest, passwordTest);
		
	}
	
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void testAddConnection_withValidEmail() throws Exception {
        String emailTestValid = "valid@email.com";

        doNothing().when(userService).addConnection(emailTestValid);

        mockMvc.perform(post("/users/add_connection")
                .param("email", emailTestValid)
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/profil"));
        
        verify(userService, times(1)).addConnection(emailTestValid);
    }
	

}
