package com.example.backend.controller;

import com.example.backend.model.dto.user.friend.FriendRequestResponse;
import com.example.backend.service.FwiendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final FwiendService fwiendService;

	@PostMapping("/add/{uuid}")
	public ResponseEntity<FriendRequestResponse> addFriend(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String uuid)  {
		return ResponseEntity.ok(new FriendRequestResponse(fwiendService.addFwiend(fwiendService.getAccountFromToken(authorizationHeader), fwiendService.getAccountFromUuid(uuid))));
	}
}
