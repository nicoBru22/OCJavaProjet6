package com.projet6.payMyBuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.TransactionRepository;
import com.projet6.payMyBuddy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionServiceTest {
	
	@Autowired
	private TransactionService transactionService;
	
	@MockBean
	private TransactionRepository transactionRepository;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;
	
	@Test
	void testGetAllTransactions() throws Exception {
		//préparation du test
		Transactions transactionTest1 = new Transactions();
		Transactions transactionTest2 = new Transactions();
		
		transactionTest1.setId(1);
		transactionTest1.setDescription("descriptionTest1");
		transactionTest1.setAmount(15.12);
		
		transactionTest2.setId(2);
		transactionTest2.setDescription("descriptionTest2");
		transactionTest2.setAmount(52.58);
		
		List<Transactions> transactionList = new ArrayList<>();
		transactionList.add(transactionTest1);
		transactionList.add(transactionTest2);
		
		when(transactionRepository.findAll()).thenReturn(transactionList);
		
		//lancement de la méthode à tester
		List<Transactions> result = transactionService.getAllTransactions();
		
		//vérifications du test
		verify(transactionRepository, times(1)).findAll();
		
		assertThat(result).isNotNull();
		assertThat(result).contains(transactionTest1);
		assertThat(result).contains(transactionTest2);
		
	}
	
	@Test
	void testAddTransaction() throws Exception {
		//préparation du test
		String senderUsernameTest = "nicolas";
		String emailSenderTest = "emailSenderTest@test.fr";
        
		String receiverUsernameTest = "sarah";
		String emailReceiverTest = "emailReceiverTest@test.fr";
		
		String passwordTest = "passwordTest";
		String roleTest = "USER";
        List<User> connectionsTest = new ArrayList<>();
        
        User senderTest = new User();
        senderTest.setId(1L);
        senderTest.setUsername(senderUsernameTest);
        senderTest.setEmail(emailSenderTest);
        senderTest.setRole(roleTest);
        senderTest.setPassword(passwordTest);
        senderTest.setConnections(connectionsTest);
        senderTest.setSolde(100);
        
        User receiverTest = new User();
        receiverTest.setId(2L);
        receiverTest.setUsername(receiverUsernameTest);
        receiverTest.setEmail(emailReceiverTest);
        receiverTest.setRole(roleTest);
        receiverTest.setPassword(passwordTest);
        receiverTest.setConnections(connectionsTest);
        receiverTest.setSolde(100);
        
        String descriptionTest = "descirptionTest";
        double amountTest = 50.00;
        
        Transactions mockedTransaction = new Transactions();
        mockedTransaction.setAmount(amountTest);
        mockedTransaction.setDescription(descriptionTest);
        mockedTransaction.setReceiver(receiverTest);
        mockedTransaction.setSender(senderTest);
        mockedTransaction.setId(0);
        mockedTransaction.setBankCommission(0.05);
        mockedTransaction.setTotalAmount(50.05);
        
        //mocks
		when(userService.getCurrentUser()).thenReturn(senderTest);
		when(userRepository.findByEmail(emailReceiverTest)).thenReturn(receiverTest);
		when(transactionRepository.save(Mockito.any(Transactions.class))).thenReturn(mockedTransaction);
		
		//méthode à tester
		Transactions result = transactionService.addTransaction(emailReceiverTest, descriptionTest, amountTest);
		
		//vérifications
		assertThat(result).isNotNull();
		assertThat(result.getAmount()).isEqualTo(amountTest);
		assertThat(result.getDescription()).isEqualTo(descriptionTest);
		assertThat(result.getSender()).isEqualTo(senderTest);
		assertThat(result.getReceiver()).isEqualTo(receiverTest);
		verify(userService, times(1)).getCurrentUser();
		verify(userRepository, times(1)).findByEmail(emailReceiverTest);
	}
	
	@Test
	void testGetALlTransactionsById() throws Exception {
		//préparation du test
		String passwordTest = "passwordTest";
		String roleTest = "USER";
        List<User> connectionsTest = new ArrayList<>();
		
        User userTest = new User();
        userTest.setId(1L);
        userTest.setUsername("nicolas");
        userTest.setEmail("nicolas@test.fr");
        userTest.setRole(roleTest);
        userTest.setPassword(passwordTest);
        userTest.setConnections(connectionsTest);
        
        User userTest2 = new User();
        userTest2.setId(1L);
        userTest2.setUsername("sarah");
        userTest2.setEmail("sarah@test.fr");
        userTest2.setRole(roleTest);
        userTest2.setPassword(passwordTest);
        userTest2.setConnections(connectionsTest);
        
        String descriptionTest = "descirptionTest";
        double amountTest = 16.23;
        
        Transactions transactionTest = new Transactions();
        transactionTest.setAmount(amountTest);
        transactionTest.setDescription(descriptionTest);
        transactionTest.setReceiver(userTest2);
        transactionTest.setSender(userTest);
        transactionTest.setId(0);
        
        List<Transactions> transactionList = new ArrayList<>();
        transactionList.add(transactionTest);
		
		//mocks
		when(userService.getCurrentUser()).thenReturn(userTest);
		when(transactionRepository.findBySender(userTest)).thenReturn(transactionList);
		
		
		//méthode à tester
		List<Transactions> result = transactionService.getAllTransactionById();
		
		//vérifications
		assertThat(result).isNotNull();
		assertThat(result).contains(transactionTest);
		verify(userService, times(1)).getCurrentUser();
		verify(transactionRepository, times (1)).findBySender(userTest);
	}




}
