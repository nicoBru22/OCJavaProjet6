package com.projet6.payMyBuddy.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionExceptionTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	
	@Test
    @WithMockUser(username = "testUser", roles = "USER")
	void addTransaction_emailEmpty_InvalidRequestException_Test() throws Exception {
		String emailTest = "";
		String descriptionTest = "description";
		String amountTest = "4";
		
		mockMvc.perform(MockMvcRequestBuilders.post("/add_transaction")
				.param("email", emailTest)
				.param("description", descriptionTest)
				.param("amount", amountTest))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/transfer"));
	}
	
	@Test
    @WithMockUser(username = "testUser", roles = "USER")
	void addTransaction_amountZero_InvalidRequestException_Test() throws Exception {
		String emailTest = "emailTest";
		String descriptionTest = "description";
		String amountTest = "0";
		
		mockMvc.perform(MockMvcRequestBuilders.post("/add_transaction")
				.param("email", emailTest)
				.param("description", descriptionTest)
				.param("amount", amountTest))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/transfer"));
	}
	
	@Test
    @WithMockUser(username = "testUser", roles = "USER")
	void addTransaction_receiverNull_InvalidRequestException_Test() throws Exception {
		String emailTest = "emailTest";
		String descriptionTest = "description";
		String amountTest = "4";
		
		mockMvc.perform(MockMvcRequestBuilders.post("/add_transaction")
				.param("email", emailTest)
				.param("description", descriptionTest)
				.param("amount", amountTest))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/transfer"));
	}
	
	@Test
    @WithMockUser(username = "testUser", roles = "USER")
	void addTransaction_SoldeInvalidException_Test() throws Exception {
		String emailTest = "emailTest";
		String descriptionTest = "description";
		String amountTest = "100";
		
		User userTest = new User();
		userTest.setSolde(10);
		userTest.setUsername("usernameTest");
		
		when(userService.getCurrentUser()).thenReturn(userTest);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/add_transaction")
				.param("email", emailTest)
				.param("description", descriptionTest)
				.param("amount", amountTest))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/transfer"));
	}

}
