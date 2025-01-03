package com.projet6.payMyBuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet6.payMyBuddy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
