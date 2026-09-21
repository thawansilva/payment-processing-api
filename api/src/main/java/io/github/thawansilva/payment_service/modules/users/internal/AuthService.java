package io.github.thawansilva.payment_service.modules.users.internal;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.thawansilva.payment_service.modules.users.api.RequestRegisterDto;
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
	
	public void register(@Valid RequestRegisterDto newUser) throws Exception {
		if (userRepo.existsByUsername(newUser.username())) {
			throw new Exception("User already exists");
		}
		String hashedPassword = passwordEncoder.encode(newUser.password());
		User createdUser = new User(newUser.username(), hashedPassword, "user");
		userRepo.save(createdUser);
	}

	public void registerAdmin(@Valid RequestRegisterDto newUser) throws Exception {
		if (userRepo.existsByUsername(newUser.username())) {
			throw new Exception("User already exists");
		}
		String hashedPassword = passwordEncoder.encode(newUser.password());
		User createdUser = new User(newUser.username(), hashedPassword, "admin");
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
			.orElseThrow(() -> new NoSuchElementException("User not found"));
	}
}