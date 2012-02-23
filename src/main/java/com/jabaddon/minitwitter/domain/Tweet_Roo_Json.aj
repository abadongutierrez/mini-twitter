// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.jabaddon.minitwitter.domain;

import com.jabaddon.minitwitter.domain.Tweet;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Tweet_Roo_Json {
    
    public String Tweet.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static Tweet Tweet.fromJsonToTweet(String json) {
        return new JSONDeserializer<Tweet>().use(null, Tweet.class).deserialize(json);
    }
    
    public static String Tweet.toJsonArray(Collection<Tweet> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<Tweet> Tweet.fromJsonArrayToTweets(String json) {
        return new JSONDeserializer<List<Tweet>>().use(null, ArrayList.class).use("values", Tweet.class).deserialize(json);
    }
    
}
