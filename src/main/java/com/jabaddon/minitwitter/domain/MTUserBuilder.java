package com.jabaddon.minitwitter.domain;

import java.util.List;
import java.util.ArrayList;

public class MTUserBuilder {
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
        return user;
    }
    
    public MTUser buildNPersist() {
        MTUser user = build();
        user.persist();
        return user;
    }
}