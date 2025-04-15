package com.example.backend.repository;

import com.example.backend.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
	Optional<Chat> findById(Long id);
}
