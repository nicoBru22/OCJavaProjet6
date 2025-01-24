package com.projet6.payMyBuddy.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.TransactionService;
import com.projet6.payMyBuddy.service.UserService;



@SpringBootTest
@AutoConfigureMockMvc
public class NavigationControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;	
	
	@MockBean
	private TransactionService transactionService;

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
    @WithMockUser(username = "testUser", roles = "USER")
    public void testAfficherPageTransfer() throws Exception {
        List<User> connections = new ArrayList<>();
        List<Transactions> transactions = new ArrayList<>();
    	
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

       when(userService.getCurrentUser()).thenReturn(userAuth);
        
        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("/profil"))
                .andExpect(model().attribute("username", "nicolas"))
                .andExpect(model().attribute("email", "nicolasTest@test.fr"))
                .andExpect(model().attribute("password", "password123"));

        verify(userService, times(1)).getCurrentUser();
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testPageProfil_UserNull() throws Exception {
    	when(userService.getCurrentUser()).thenReturn(null);
    	mockMvc.perform(get("/profil"))
    		.andExpect(status().is3xxRedirection())
    		.andExpect(redirectedUrl("/login")); 
    	
    	verify(userService, times(1)).getCurrentUser();    	
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testAfficherPageTransfer_Exception() throws Exception {
        when(userService.getConnections()).thenThrow(new RuntimeException("Erreur simulée pour les connexions"));

        mockMvc.perform(get("/transfer"))
            .andExpect(status().isOk())
            .andExpect(view().name("error"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Une erreur s'est produite lors du chargement des données."));

        verify(userService, times(1)).getConnections();
    }

 

}
