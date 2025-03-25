package com.projet6.payMyBuddy.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.service.TransactionService;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private TransactionService transactionService;
	
	@Test
    @WithMockUser(username = "testUser", roles = "USER")
	void testAddTransaction() throws Exception {
		
		String emailTest = "email@test.fr";
		String descriptionTest = "test description de la transaction";
		double amountTest = 12.34;
		
		Transactions transactionTest = new Transactions();
		transactionTest.setId(1L);
		transactionTest.setAmount(amountTest);
		transactionTest.setDescription(descriptionTest);
		transactionTest.setReceiver(null);
		transactionTest.setSender(null);
		
		when(transactionService.addTransaction(emailTest, descriptionTest, amountTest)).thenReturn(transactionTest);
		
		mockMvc.perform(post("/add_transaction")
				.param("email", emailTest)
				.param("description", descriptionTest)
		        .param("amount", String.valueOf(amountTest)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/transfer"));
	}

}
