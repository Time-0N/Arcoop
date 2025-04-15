package com.example.backend.Seeder;

import com.example.backend.model.entity.Account;
import com.example.backend.model.entity.Color;
import com.example.backend.model.enums.Role;
import com.example.backend.repository.ColorRepository;
import com.example.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Configuration
@Profile("dev")
public class DatabaseSeeder {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ColorRepository colorRepository;

	@PostConstruct
	public void init() {
		seedDatabase();
	}

	@Transactional
	public void seedDatabase() {
		seedColors();
		if (userRepository.count() == 0) {
			seedUsers();
		}
	}

	@Transactional
	public void seedColors() {
		if (colorRepository.count() == 0) {
			createAndSaveColor("White", "#FFFFFF", BigDecimal.ZERO);
			createAndSaveColor("Yellow", "#FFFF00", BigDecimal.ZERO);
			createAndSaveColor("Purple", "#800080", new BigDecimal("200"));
			createAndSaveColor("Green", "#008000", new BigDecimal("300"));
		}
	}

	@Transactional
	public void seedUsers() {
		saveAccount("Shrek", ".Shrekinat0r", "xX_Shrek_Xx", BigDecimal.valueOf(999999));
		saveAccount("Silvan", ".0nly_cool_if_he_gives_me_4_stars_in_my_praxischeck", "SilvanKammermann", BigDecimal.valueOf(-1000));
		saveAccount("Samira Tes(t)daten", ".Tes(t)daten1", "OwnerOfKitcord", BigDecimal.valueOf(0));
		saveAccount("Juanita", ".RucolaP4armesan", "Juacamole", BigDecimal.valueOf(0));
		saveAccount("Justin Schumacher", ".B3stMMA", "Giga Chad", BigDecimal.valueOf(9999999));
	}

	private void createAndSaveColor(String name, String code, BigDecimal price) {
		Color color = new Color();
		color.setColorName(name);
		color.setColorCode(code);
		color.setPrice(price);
		colorRepository.save(color);
	}

	private void saveAccount(String username, String rawPassword, String name, BigDecimal medals) {
		// Ensure that the color "White" exists before trying to fetch it
		Optional<Color> optionalColor = colorRepository.findColorByName("White");
		Color color;
		if (optionalColor.isEmpty()) {
			// If not found, create and save the color
			color = new Color();
			color.setColorName("White");
			color.setColorCode("#FFFFFF");
			color.setPrice(BigDecimal.ZERO);
			colorRepository.save(color);
		} else {
			color = optionalColor.get();
		}

		var account = Account.builder()
				.username(username)
				.password(passwordEncoder.encode(rawPassword))
				.name(name)
				.role(Role.USER)
				.medals(medals)
				.equippedColor("#FFFFFF")
				.uuid(Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes()).substring(0, 6).toUpperCase())
				.colors(Set.of(color))
				.build();
		userRepository.save(account);
	}
}

