package com.example.backend.controller;

import com.example.backend.exception.NotFoundException;
import com.example.backend.model.dto.user.color.ColorResponse;
import com.example.backend.model.entity.Account;
import com.example.backend.service.AccountService;
import com.example.backend.service.JwtService;
import com.example.backend.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

	private final ShopService shopService;
	private final JwtService jwtService;
	private final AccountService accountService;

	@GetMapping("/colors")
	public ResponseEntity<List<ColorResponse>> getAvailableColors(@RequestHeader("Authorization") String authorizationHeader) {
		Account account = getAccountFromToken(authorizationHeader);
		return ResponseEntity.of(Optional.ofNullable(shopService.getAvailableColors(account)));
	}

	@PostMapping("/colors/buy")
	public ResponseEntity<ColorResponse> buyColor(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long colorId) throws InstantiationException {
		Account account = getAccountFromToken(authorizationHeader);
		return ResponseEntity.ok(new ColorResponse(shopService.buyColor(account, colorId), account));
	}

	@PostMapping("/colors/equip")
	public ResponseEntity<ColorResponse> equipColor(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Long colorId) throws InstantiationException {
		Account account = getAccountFromToken(authorizationHeader);
		return ResponseEntity.ok(new ColorResponse(shopService.equipColor(account, colorId), account));
	}

	private Account getAccountFromToken(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String username = jwtService.extractUsername(token);
		return accountService.findByUsername(username).orElseThrow(() -> new NotFoundException("Account doesn't exist"));
	}
}
