package com.jabaddon.minitwitter.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.op4j.Op;
import org.op4j.functions.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "mt_t_user", finders = {"findMTUsersByUsernameEquals", "findMTUsersByUsernameLikeOrNameLikeOrLastNameLike"})
@RooJson
public class MTUser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MTUser.class);

    @NotNull
    @Size(max = 50)
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

    public List<Tweet> getTimeline() {
        List<Long> ids = Op.onList(this.getFollowing()).map(Get.attrOfLong("id")).get();
        ids.add(this.getId());
        TypedQuery<Tweet> tq = entityManager().createQuery(
                "select t from Tweet t where t.author.id in (:ids) order by t.timestamp asc", Tweet.class);
        tq.setParameter("ids", ids);
        return tq.getResultList();
    }
    
    public List<Tweet> getTimelineSince(Date date) {
        List<Long> ids = Op.onList(this.getFollowing()).map(Get.attrOfLong("id")).get();
        ids.add(this.getId());
        TypedQuery<Tweet> tq = entityManager().createQuery(
                "select t from Tweet t where t.author.id in (:ids) and t.timestamp > :since order by t.timestamp asc", Tweet.class);
        tq.setParameter("ids", ids);
        tq.setParameter("since", date);
        return tq.getResultList();
    }
    
    public boolean isFollowing(Long idUser) {
        return this.getFollowing().contains(MTUser.findMTUser(idUser));
    }

    public void unfollow(MTUser userToUnfollow) {
        this.getFollowing().remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(this);
    }

    public static TypedQuery<MTUser> findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(String username, String name, String lastName) {
        boolean usernameAdded = false;
        boolean nameAdded = false;
        boolean lastNameAdded = false;
        String criteria = "";
        if (username == null || username.length() == 0) {        
        }
        else {
            username = username.replace('*', '%');
            if (username.charAt(0) != '%') {
                username = "%" + username;
            }
            if (username.charAt(username.length() - 1) != '%') {
                username = username + "%";
            }
            criteria += " OR LOWER(o.username) LIKE LOWER(:username) ";
            usernameAdded = true;
        }

        if (name == null || name.length() == 0) {            
        }
        else {
            name = name.replace('*', '%');
            if (name.charAt(0) != '%') {
                name = "%" + name;
            }
            if (name.charAt(name.length() - 1) != '%') {
                name = name + "%";
            }
            criteria += " OR LOWER(o.name) LIKE LOWER(:name) ";
            nameAdded = true;
        }

        if (lastName == null || lastName.length() == 0) {            
        }
        else {
            lastName = lastName.replace('*', '%');
            if (lastName.charAt(0) != '%') {
                lastName = "%" + lastName;
            }
            if (lastName.charAt(lastName.length() - 1) != '%') {
                lastName = lastName + "%";
            }
            criteria += " OR LOWER(o.lastName) LIKE LOWER(:lastName) ";
            lastNameAdded = true;
        }
        String query = "SELECT o FROM MTUser AS o WHERE 1 = 2 " + criteria;
        EntityManager em = MTUser.entityManager();
        TypedQuery<MTUser> q = em.createQuery(query, MTUser.class);
        if (usernameAdded) {
            q.setParameter("username", username);
        }
        if (nameAdded) {
            q.setParameter("name", name);
        }
        if (lastNameAdded) {
            q.setParameter("lastName", lastName);
        }
        return q;
    }
}
