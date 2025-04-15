package com.example.backend.repository;

import com.example.backend.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Account, Integer> {

	Optional<Account> findById(Long id);
	Optional<Account> findByUsername(String username);
	Optional<Account> findByUuid(String uuid);
	boolean existsByUsername(String username);
	boolean existsByName(String name);
}
