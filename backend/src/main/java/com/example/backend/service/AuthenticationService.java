package com.example.backend.service;


import com.example.backend.model.dto.auth.AuthenticationRequest;
import com.example.backend.model.dto.auth.AuthenticationResponse;
import com.example.backend.model.dto.user.RegisterRequest;
import com.example.backend.model.entity.Account;
import com.example.backend.model.entity.Color;
import com.example.backend.model.enums.Role;
import com.example.backend.repository.ColorRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final ColorRepository colorRepository;

	public AuthenticationResponse register(RegisterRequest request) {
		if (repository.existsByUsername(request.getUsername())) {
			throw new RuntimeException("Username already exists");
		}
		if (repository.existsByName(request.getName())) {
			throw new RuntimeException("Name already exists");
		}
		if (!checkPasswordForCredentials(request.getPassword())) {
			throw new RuntimeException("Missing Password credentials");
		}
		if (!checkNameForCredentials(request.getName())) {
			throw  new RuntimeException("Name must be not more than 16 characters");
		}

		Color defaultColor = colorRepository.findColorByName("White").orElseThrow();

		var account = Account.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.name(request.getName())
				.role(Role.USER)
				.medals(BigDecimal.valueOf(0))
				.equippedColor("#FFFFFF")
				.uuid(Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes()).substring(0, 6).toUpperCase())
				.colors(Set.of(defaultColor))
				.build();
		repository.save(account);
		var jwtToken = jwtService.generateToken(account);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword()
				)
		);
		var account = repository.findByUsername(request.getUsername())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(account);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public boolean checkPasswordForCredentials(String password) {
		//Password Flags
		boolean upperCaseFlag = false;
		boolean lowercaseFlag = false;
		boolean numberFlag = false;
		boolean specialCharacterFlag = false;
		if (password.length() < 6) {
			return false;
		}

		for (int i = 0; i < password.length(); i++) {

			char ch = password.charAt(i);
			if (Character.isLowerCase(ch)) {
				lowercaseFlag = true;
			}
			if (Character.isUpperCase(ch)) {
				upperCaseFlag = true;
			}
			if (Character.isDigit(ch)) {
				numberFlag = true;
			}
			if (!Character.isLetterOrDigit(ch)) {
				specialCharacterFlag = true;
			}
		}
		return (upperCaseFlag && lowercaseFlag && numberFlag && specialCharacterFlag);
	}

	public boolean checkNameForCredentials(String name) {
		return (name.length() <= 16);
	}
}
