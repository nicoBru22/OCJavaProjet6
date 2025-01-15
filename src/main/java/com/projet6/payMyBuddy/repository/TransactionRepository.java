package com.projet6.payMyBuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {
	List<Transactions> findBySender(User user);
}
