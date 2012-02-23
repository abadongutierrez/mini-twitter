package com.jabaddon.minitwitter.domain;

import java.util.Collection;
import java.util.Date;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import flexjson.JSONSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "mt_t_tweet")
@RooJson
public class Tweet {

    @NotNull
    @Size(max = 140)
    private String text;

    @NotNull
    @DateTimeFormat(style = "M-")
    private Date timestamp;

    @ManyToOne(fetch = FetchType.EAGER)
    private MTUser author;

    public Tweet() {
    }

    public Tweet(String text, Date timestamp, MTUser author) {
        this.text = text;
        this.timestamp = timestamp;
        this.author = author;
    }
    
    public String getAuthorUsername() {
        return this.author.getUsername();
    }

    public static String toJsonArrayWithoutMTUserDetails(Collection<Tweet> collection) {
        return new JSONSerializer().exclude("*.class").exclude("author").include("authorUsername").serialize(collection);
    }
}
