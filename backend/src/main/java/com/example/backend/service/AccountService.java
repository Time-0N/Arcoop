package com.example.backend.service;

import com.example.backend.exception.NotFoundException;
import com.example.backend.model.dto.user.UpdateUserRequest;
import com.example.backend.model.dto.user.UserResponse;
import com.example.backend.model.entity.Account;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AuthenticationService authenticationService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final Predicate<String> stringCheck = (stringVal) -> stringVal != null && !stringVal.isBlank();
	private final GameService gameService;

	public UserResponse getAccount(String username) {
		Optional<Account> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			return new UserResponse(user.get());
		}
		throw new NotFoundException("User not found");
	}

	public Integer getUserIdByUsername(String username) {
		return userRepository.findByUsername(username).map(Account::getId).orElseThrow(() -> new NotFoundException("User not found"));
	}

	public Account updateUser(Integer id, UpdateUserRequest request) {
		Account user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

		if (stringCheck.test(request.getUsername())) {
			if (!userRepository.existsByUsername(request.getUsername())) {
				user.setUsername(request.getUsername());
			}
		}

		if (stringCheck.test(request.getPassword())) {
			if (authenticationService.checkPasswordForCredentials(request.getPassword())) {
				user.setPassword(passwordEncoder.encode(request.getPassword()));
			}  else throw new RuntimeException("Missing Password credentials");
		}

		if (stringCheck.test(request.getName())) {
			if (authenticationService.checkNameForCredentials(request.getName())) {
				user.setName(request.getName());
			}  else throw new RuntimeException("Name must be not more than 16 characters");
		}

		return userRepository.save(user);
	}

	public Optional<Account> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public void deleteUser(Integer userId) {
		Account accountToDelete = userRepository.findById(userId).orElseThrow();
		if (gameService.IsUserInGame(accountToDelete)) {
			gameService.leaveGame(accountToDelete);
		}
		userRepository.deleteById(userId);
	}

}

