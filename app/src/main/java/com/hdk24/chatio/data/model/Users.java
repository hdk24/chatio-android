package com.hdk24.chatio.data.model;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public class Users {

    private String username;

    private Users() {
    }

    public String getUsername() {
        return username;
    }

    public static class Builder {
        private String username;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Users build() {
            Users users = new Users();
            users.username = username;
            return users;
        }
    }
}
