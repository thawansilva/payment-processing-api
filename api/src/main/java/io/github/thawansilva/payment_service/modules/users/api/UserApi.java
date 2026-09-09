package io.github.thawansilva.payment_service.modules.users.api;

import java.util.UUID;

public interface UserApi {
    public UserView getUserById(UUID id);
    public boolean userExists(UUID id);
}
