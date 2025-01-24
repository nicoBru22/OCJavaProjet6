package com.projet6.payMyBuddy.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserService userService;
	
    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    @MockBean
    private OAuth2User oAuth2User;

    @MockBean
    private UserDetails userDetails;
    
    @MockBean
    private UserRepository userRepository;
	
	@Test
	@WithMockUser(username = "nicolas@test.fr", roles = "USER")
	void testGetCurrentUser_InstanceOfUserDetail() throws Exception {
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
		
        when(userRepository.findByEmail("nicolas@test.fr")).thenReturn(userTest);
		
		User result = userService.getCurrentUser();
		
	    assertThat(result).isNotNull();
	    assertThat(result.getUsername()).isEqualTo(usernameTest);
	    assertThat(result.getEmail()).isEqualTo(emailTest);
		
		verify(userRepository, times(1)).findByEmail("nicolas@test.fr");
		
	}

}
