package com.example.backend.controller;

import com.example.backend.model.dto.auth.AuthenticationRequest;
import com.example.backend.model.dto.auth.AuthenticationResponse;
import com.example.backend.model.dto.user.RegisterRequest;
import com.example.backend.service.AuthenticationService;
import com.example.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;
	private final JwtService jwtService;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(
			@RequestBody RegisterRequest request
	) {
		return ResponseEntity.ok(service.register(request));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(
			@RequestBody AuthenticationRequest request
	) {
		return ResponseEntity.ok(service.authenticate(request));
	}
}

