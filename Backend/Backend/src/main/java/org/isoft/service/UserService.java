package org.isoft.service;

import org.isoft.Entity.User;

public interface UserService {
    User register(String username, String password);
    boolean login(String username, String password);
}
