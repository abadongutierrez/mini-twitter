// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.jabaddon.minitwitter.domain;

import com.jabaddon.minitwitter.domain.MTUser;
import com.jabaddon.minitwitter.domain.Tweet;
import java.util.Date;

privileged aspect Tweet_Roo_JavaBean {
    
    public String Tweet.getText() {
        return this.text;
    }
    
    public void Tweet.setText(String text) {
        this.text = text;
    }
    
    public Date Tweet.getTimestamp() {
        return this.timestamp;
    }
    
    public void Tweet.setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public MTUser Tweet.getAuthor() {
        return this.author;
    }
    
    public void Tweet.setAuthor(MTUser author) {
        this.author = author;
    }
    
}
