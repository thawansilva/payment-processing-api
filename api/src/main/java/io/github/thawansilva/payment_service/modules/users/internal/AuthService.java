package io.github.thawansilva.payment_service.modules.users.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}