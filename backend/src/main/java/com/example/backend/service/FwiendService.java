package com.example.backend.service;

import com.example.backend.exception.NotFoundException;
import com.example.backend.model.entity.Account;
import com.example.backend.model.entity.Chat;
import com.example.backend.repository.ChatRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FwiendService {

	private final JwtService jwtService;
	private final AccountService accountService;
	private final UserRepository userRepository;
	private final ChatRepository chatRepository;

	public Account addFwiend(Account requester, Account receiver) {
		if (requester != receiver) {

		}
		Chat chat = new Chat();
		chat.getAccount().add(receiver);
		chat.getAccount().add(requester);
		chatRepository.save(chat);
		return receiver;
	}

	public Account getAccountFromUuid(String uuid) {
		if (userRepository.findByUuid(uuid).isPresent()) {
			return userRepository.findByUuid(uuid).get();
		} else throw new NotFoundException("Invalid UUID");
	}

	public Account getAccountFromToken(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String username = jwtService.extractUsername(token);
		return accountService.findByUsername(username).orElseThrow(() -> new NotFoundException("Account doesn't exist"));
	}
}
