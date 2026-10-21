package io.github.thawansilva.payment_service.modules.users.internal;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.thawansilva.payment_service.modules.exception.api.UserNotFound;
import io.github.thawansilva.payment_service.modules.exception.api.UsernameAlreadyExists;
import io.github.thawansilva.payment_service.modules.users.api.RegisterRequestDto;
import jakarta.validation.Valid;

@Service
public class AuthService {
	@Autowired 
	final private UserRepository userRepo;
	@Autowired 
	final private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
	
	public void register(@Valid RegisterRequestDto newUser) {
		if (userRepo.existsByUsername(newUser.username())) {
			throw new UsernameAlreadyExists(newUser.username());
		}
		String hashedPassword = passwordEncoder.encode(newUser.password());
		User createdUser = new User(newUser.username(), hashedPassword, "USER");
		userRepo.save(createdUser);
	}

	public void registerAdmin(@Valid RegisterRequestDto newUser) {
		if (userRepo.existsByUsername(newUser.username())) {
			throw new UsernameAlreadyExists(newUser.username());
		}
		String hashedPassword = passwordEncoder.encode(newUser.password());
		User createdUser = new User(newUser.username(), hashedPassword, "ADMIN");
		userRepo.save(createdUser);
	}

	public UUID getIdByUsername(String username) {
		return userRepo.findByUsername(username)
			.orElseThrow(() -> new AuthenticationException("Invalid credentials") {
			})
			.getId();
	}

	public User getUserById(UUID id) {
		return userRepo.findById(id)
			.orElseThrow(() -> new UserNotFound());
	}
}