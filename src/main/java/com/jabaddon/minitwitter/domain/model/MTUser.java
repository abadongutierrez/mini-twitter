package com.jabaddon.minitwitter.domain.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Table(name = "mt_t_user")
@Entity
public class MTUser {

    @NotNull
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotNull
    @Size(max = 10)
    private String password;

    @Transient
    private String passwordConfirmation;

    @NotNull
    private boolean enabled;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 50)
    private String lastName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "mt_t_user_tweets",
            joinColumns = @JoinColumn(name = "id_author"),
            inverseJoinColumns = @JoinColumn(name = "id_tweet"))
    private List<Tweet> tweets = new ArrayList<Tweet>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "mt_t_user_following",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_following"))
    private List<MTUser> following = new ArrayList<MTUser>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "mt_t_user_followers",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_follower"))
    private List<MTUser> followers = new ArrayList<MTUser>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Version
    @Column(name = "version")
    private Integer version;

    public static Collection<MTUser> fromJsonArrayToMTUsers(String json) {
        return new JSONDeserializer<List<MTUser>>()
        .use("values", MTUser.class).deserialize(json);
    }

    public static String toJsonArray(Collection<MTUser> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<MTUser> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

    public static MTUser fromJsonToMTUser(String json) {
        return new JSONDeserializer<MTUser>()
        .use(null, MTUser.class).deserialize(json);
    }

    public void tweet(String text) {
        tweetOnDate(text, new Date());
    }
    
    public void tweetOnDate(String text, Date date) {
        Tweet t = new Tweet(text, date, this);
        this.getTweets().add(t);
    }

    public void follow(MTUser user) {
        user.getFollowers().add(this);
        this.getFollowing().add(user);
    }
    
    public int countFollowing() {
        return this.getFollowing().size();
    }
    
    public int countFollowers() {
        return this.getFollowers().size();
    }

    public void unfollow(MTUser userToUnfollow) {
        this.getFollowing().remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(this);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

    public void setFollowers(List<MTUser> followers) {
        this.followers = followers;
    }

    public List<MTUser> getFollowers() {
        return this.followers;
    }

    public void setFollowing(List<MTUser> following) {
        this.following = following;
    }

    public List<MTUser> getFollowing() {
        return this.following;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public List<Tweet> getTweets() {
        return this.tweets;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return this.passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
