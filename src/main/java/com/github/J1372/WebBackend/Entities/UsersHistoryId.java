package com.github.J1372.WebBackend.Entities;

import java.io.Serializable;

public class UsersHistoryId implements Serializable {

    private User a;
    private User b;

    public UsersHistoryId(User a, User b) {
        this.a = a;
        this.b = b;
    }

}
