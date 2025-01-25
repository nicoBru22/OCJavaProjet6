package com.projet6.payMyBuddy.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


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
	
	@Test
	void testGetCurrentUser_withOauth() throws Exception {
	    // Mock de l'utilisateur OAuth2 avec des attributs définis
	    OAuth2User oAuth2User = mock(OAuth2User.class);
	    when(oAuth2User.getAttribute("login")).thenReturn("nicolas");
	    when(oAuth2User.getAttribute("email")).thenReturn("nicolas@test.fr");

	    // Création d'une liste d'autorités (par exemple ROLE_USER)
	    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

	    // Création d'un token d'authentification OAuth2
	    OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(
	        oAuth2User, authorities, "oauth-client-id"
	    );

	    // Injection de l'authentification dans le contexte de sécurité
	    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

	    // Mock de la méthode findByEmail pour retourner un utilisateur fictif
		String usernameTest = "nicolas";
		String emailTest = "nicolas@test.fr";
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

	    // Appel de la méthode à tester
	    User user = userService.getCurrentUser();

	    // Vérification de la réponse
	    assertThat(user).isNotNull();
	    assertThat(user.getUsername()).isEqualTo("nicolas");
	    assertThat(user.getEmail()).isEqualTo("nicolas@test.fr");

	    // Vérification que la méthode getUserWithOauth a bien été appelée
	    verify(userRepository, times(1)).findByEmail(emailTest);
	}


	
	@Test
	@WithMockUser(username = "userTest", roles = "USER")
	void testGetAllUser() throws Exception {
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
		
		when(userRepository.findAll()).thenReturn(userListTest);
		
		List<User> result = userService.getAllUser();
		
	    assertThat(result).isNotNull();
	    assertThat(result).isNotEmpty();
		assertThat(result).contains(userTest);
		
		verify(userRepository, times(1)).findAll();
	}
	
	@Test
	void testAddUser() throws Exception {
	    String usernameTest = "nicolas";
	    String passwordTest = "password";
	    String emailTest = "email@test.fr";
	    String encodedPassword = "encodedPassword";

	    BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
	    when(encoder.encode(passwordTest)).thenReturn(encodedPassword);

	    User mockedUser = new User();
	    mockedUser.setId(1L);
	    mockedUser.setUsername(usernameTest);
	    mockedUser.setEmail(emailTest);
	    mockedUser.setPassword(encodedPassword);
	    mockedUser.setRole("user");

	    when(userRepository.save(Mockito.any(User.class))).thenReturn(mockedUser);

	    User result = userService.addUser(usernameTest, emailTest, passwordTest);

	    assertThat(result.getUsername()).isEqualTo(usernameTest);
	    assertThat(result.getEmail()).isEqualTo(emailTest);
	    assertThat(result.getRole()).isEqualTo("user");
	    assertThat(result.getPassword()).isEqualTo(encodedPassword);

	    verify(userRepository, times(1)).save(Mockito.any(User.class));
	}
	
	@Test
	@WithMockUser(username = "email@test.fr", roles = "USER")
	void testGetConnections() throws Exception {
		//préparation du test
	    User connection1 = new User();
	    connection1.setId(2L);
	    connection1.setUsername("connection1");

	    User connection2 = new User();
	    connection2.setId(3L);
	    connection2.setUsername("connection2");

	    List<User> connectionsTest = new ArrayList<>();
	    connectionsTest.add(connection1);
	    connectionsTest.add(connection2);

	    User userTest = new User();
	    userTest.setId(1L);
	    userTest.setUsername("nicolas");
	    userTest.setEmail("email@test.fr");
	    userTest.setPassword("passwordTest");
	    userTest.setRole("USER");
	    userTest.setConnections(connectionsTest);

	    //mock
	    when(userRepository.findByEmail("email@test.fr")).thenReturn(userTest);

	    //méthode à tester
	    List<User> result = userService.getConnections();

	    //vérifications
	    verify(userRepository, times(1)).findByEmail("email@test.fr");
	    assertThat(result).isNotNull();
	    assertThat(result).hasSize(2);
	    assertThat(result).contains(connection1, connection2);
	}
	
	@Test
	@WithMockUser(username = "nicolas@test.fr", roles = "USER")
	void testAddConnection() throws Exception {
		//préparation du test
	    List<User> connectionsTest1 = new ArrayList<>();
	    User userTest1 = new User();
	    userTest1.setId(1L);
	    userTest1.setUsername("nicolas");
	    userTest1.setEmail("nicolas@test.fr");
	    userTest1.setPassword("passwordTest");
	    userTest1.setRole("USER");
	    userTest1.setConnections(connectionsTest1);
	    
	    List<User> connectionsTest2 = new ArrayList<>();
	    User userTest2 = new User();
	    userTest2.setId(2L);
	    userTest2.setUsername("sarah");
	    userTest2.setEmail("sarah@test.fr");
	    userTest2.setPassword("passwordTest");
	    userTest2.setRole("USER");
	    userTest2.setConnections(connectionsTest2);

	    //mocks
	    when(userRepository.findByEmail("nicolas@test.fr")).thenReturn(userTest1);
	    when(userRepository.findByEmail("sarah@test.fr")).thenReturn(userTest2);
	    
	    //méthode à tester
	    userService.addConnection(userTest2.getEmail());
	    
	    //vérifications
	    verify(userRepository, times(1)).findByEmail(userTest1.getEmail());
	    verify(userRepository, times(1)).findByEmail(userTest2.getEmail());
	    verify(userRepository, times(1)).save(userTest1);
	    verify(userRepository, times(1)).save(userTest2);
	    assertThat(userTest1.getConnections()).contains(userTest2);
	    assertThat(userTest2.getConnections()).contains(userTest1);
	}



}
