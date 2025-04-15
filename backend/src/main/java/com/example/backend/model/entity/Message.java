package com.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "message")
@Data
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	private Account account;

	private String content;

	@ManyToOne
	@JoinColumn(name = "chat")
	private Chat chat;
}
