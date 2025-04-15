package com.example.backend.model.entity;

//import com.example.backend.Initialization.ColorInitializer;
//import jakarta.annotation.PostConstruct;
import com.example.backend.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account implements UserDetails {

	@Id
	@GeneratedValue
	private Integer id;
	private String username;
	private String password;
	private String name;
	private BigDecimal medals;
	private String equippedColor;
	private String uuid;

	@Enumerated(EnumType.STRING)
	private Role role;

	@ManyToMany
	@JoinTable(
			name = "account_color",
			joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "color_id", referencedColumnName = "id")
	)
	private Set<Color> colors = new HashSet<>();

	@ManyToMany
	@JoinTable(
			name = "account_chats",
			joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "color_id", referencedColumnName = "id")
	)
	private Set<Chat> chats = new HashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void incrementMedals(int increment) {
		this.medals = this.medals.add(BigDecimal.valueOf(increment));
	}

	public void decrementMedals(int decrement) {
		medals = medals.subtract(BigDecimal.valueOf(decrement));
	}
}