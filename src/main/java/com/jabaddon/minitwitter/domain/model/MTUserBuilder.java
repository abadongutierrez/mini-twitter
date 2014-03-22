package com.jabaddon.minitwitter.domain.model;

import java.util.List;
import java.util.ArrayList;

public class MTUserBuilder {
    private String password = "1234567890";
    private String username = "default";
    private String name = "Rafael";
    private String lastName = "Gutierrez";
    private List<Tweet> tweets = new ArrayList<Tweet>();

    public MTUserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public MTUser build() {
        MTUser user = new MTUser();
        user.setUsername(this.username);
        user.setName(this.name);
        user.setLastName(this.lastName);
        user.setTweets(this.tweets);
        user.setPassword(this.password);
        return user;
    }
}
