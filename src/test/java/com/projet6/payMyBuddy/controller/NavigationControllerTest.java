package com.projet6.payMyBuddy.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.UserService;



@SpringBootTest
@AutoConfigureMockMvc
public class NavigationControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;

	@Test
	public void testAfficherPageLogIn() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"));
	}

	@Test
	public void testAfficherPageSignIn() throws Exception {
		mockMvc.perform(get("/signin"))
			.andExpect(status().isOk())
			.andExpect(view().name("signin"));
	}
	
	@Test
	public void testRedirectionIfUnauthorized() throws Exception {
		mockMvc.perform(get("/profil"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/login")); 
	}
	
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testAccessWithAuthentication() throws Exception {

    	mockMvc.perform(get("/add_relation"))
                .andExpect(status().isOk())
                .andExpect(view().name("add_relation"));
    }
    
    @Test
    @WithMockUser(username = "nicolas", roles = "USER")
    public void testAfficherPageTransfer() throws Exception {
        User userAuth = new User();
        List<User> connections = new ArrayList<>();
        List<Transactions> transactions = new ArrayList<>();
        
        userAuth.setId(1L);
        userAuth.setUsername("nicolas");
        userAuth.setEmail("nicolasTest@test.fr");
        userAuth.setRole("user");
        userAuth.setPassword("password123");
        userAuth.setConnections(connections);
    	
        mockMvc.perform(get("/transfer"))
        	.andExpect(status().isOk())
        	.andExpect(view().name("transfer"))
        	.andExpect(model().attribute("connections", connections))
        	.andExpect(model().attribute("transactions", transactions));
    }
    
    @Test
    public void testAfficherPageTransfer_unauthorizeUser() throws Exception {
        mockMvc.perform(get("/transfer"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/login")); 
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void testAfficherPageAccueil() throws Exception {
    	mockMvc.perform(get("/"))
    		.andExpect(status().isOk())
    		.andExpect(view().name("profil"));
    }
    
    
	
    @Test
    @WithMockUser(username = "nicolas", roles = "USER")
    void testAfficherPageUserProfil_userAuthenticated() throws Exception {
        User userAuth = new User();
        List<User> connections = new ArrayList<>();
        userAuth.setId(1L);
        userAuth.setUsername("nicolas");
        userAuth.setEmail("nicolasTest@test.fr");
        userAuth.setRole("user");
        userAuth.setPassword("password123");
        userAuth.setConnections(connections);

        Mockito.when(userService.getCurrentUser()).thenReturn(userAuth);
        
        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("/profil"))
                .andExpect(model().attribute("username", "nicolas"))
                .andExpect(model().attribute("email", "nicolasTest@test.fr"))
                .andExpect(model().attribute("password", "password123"));

        Mockito.verify(userService, times(1)).getCurrentUser();
    }
 

}
