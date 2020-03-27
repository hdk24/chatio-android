package com.hdk24.chatio.data.model;

/*
 *  Created by Hanantadk on 24/03/20.
 *  Copyright (c) 2020. All rights reserved.
 *  Last modified 24/03/20.
 */
public class Message {

    private int type;
    private String text;
    private String username;
    private long time;
    private boolean isPrivate;

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public long getTime() {
        return time;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Message() {

    }

    public static class Builder {
        private final int type;
        private String username;
        private String text;
        private long time;
        private boolean isPrivate;

        public Builder(int type) {
            this.type = type;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder message(String text) {
            this.text = text;
            return this;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Builder isPrivate(boolean privates) {
            this.isPrivate = privates;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.type = type;
            message.username = username;
            message.text = text;
            message.time = time;
            message.isPrivate = isPrivate;
            return message;
        }
    }
}
