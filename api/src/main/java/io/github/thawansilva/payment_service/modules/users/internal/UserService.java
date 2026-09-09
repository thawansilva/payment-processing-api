package io.github.thawansilva.payment_service.modules.users.internal;

import java.util.UUID;

import io.github.thawansilva.payment_service.modules.users.api.UserApi;
import io.github.thawansilva.payment_service.modules.users.api.UserView;

public class UserService implements UserApi {
    @Override 
    public UserView getUserById(UUID id)
    {
        return new UserView("teste", "Thawan", "ADMIN");
    }

    @Override 
    public boolean userExists(UUID id)
    {
        return true;
    }
}
