package com.projet6.payMyBuddy.exception;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserExceptionTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void addConnexion_EmailEmpty_InvalidRequestException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/add_connection")
                .param("email", "")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add_relation"))
                .andExpect(flash().attribute("error", containsString("L'adresse email est invalide.")));
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void addConnexion_UserToAdd_NotFoundException() throws Exception {
    	String emailTest = "test";
    	
    	when(userRepository.findByEmail(emailTest)).thenReturn(null);
    	
    	mockMvc.perform(MockMvcRequestBuilders.post("/users/add_connection")
                .param("email", emailTest)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add_relation"))
                .andExpect(flash().attribute("error", containsString("L'utilisateur à cette adresse mail n'existe pas.")));
    }
    
    
    
    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void addUser_userNameEmpty_UserRequestAddInvalidException() throws Exception {
    	String usernameTest = "";
    	String emailTest = "email@test.fr";
    	String passwordTest ="passwordTest";
    	
    	mockMvc.perform(MockMvcRequestBuilders.post("/users/add_user")
                .param("email", emailTest)
                .param("username", usernameTest)
                .param("password", passwordTest)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }
    
    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void addUser_userExist_UserExistException() throws Exception {
    	String usernameTest = "userNameTest";
    	String emailTest = "email@test.fr";
    	String passwordTest ="passwordTest";
    	
    	User userTest = new User();
    	userTest.setUsername(usernameTest);
    	
    	when(userRepository.findByEmail(emailTest)).thenReturn(userTest);
    	
    	mockMvc.perform(MockMvcRequestBuilders.post("/users/add_user")
    			.with(csrf())
                .param("email", emailTest)
                .param("username", usernameTest)
                .param("password", passwordTest))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }

}
