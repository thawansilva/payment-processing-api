package io.github.thawansilva.payment_service.modules.users.internal;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
	final private AuthRepository authRepo;

    public AuthService(AuthRepository authRepo) {
        this.authRepo = authRepo;
    }
	
	public void registerUser(RequestRegisterDto user) {
	}

}