package com.jabaddon.minitwitter.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Table(name = "mt_t_tweet")
@Entity
public class Tweet {

    public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("text", "timestamp", "author");
    @PersistenceContext
    transient EntityManager entityManager;
    @NotNull
    @Size(max = 140)
    private String text;

    @NotNull
    @DateTimeFormat(style = "M-")
    private Date timestamp;

    @ManyToOne(fetch = FetchType.EAGER)
    private MTUser author;
    @Version
    @Column(name = "version")
    private Integer version;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    public Tweet() {
    }

    public Tweet(String text, Date timestamp, MTUser author) {
        this.text = text;
        this.timestamp = timestamp;
        this.author = author;
    }

    public static List<Tweet> findAllTweets() {
        return entityManager().createQuery("SELECT o FROM Tweet o", Tweet.class).getResultList();
    }

    public static long countTweets() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Tweet o", Long.class).getSingleResult();
    }

    public static final EntityManager entityManager() {
        EntityManager em = new Tweet().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static List<Tweet> findAllTweets(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Tweet o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Tweet.class).getResultList();
    }

    public static Tweet findTweet(Long id) {
        if (id == null) return null;
        return entityManager().find(Tweet.class, id);
    }

    public static List<Tweet> findTweetEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Tweet o", Tweet.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Tweet> findTweetEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Tweet o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Tweet.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static Collection<Tweet> fromJsonArrayToTweets(String json) {
        return new JSONDeserializer<List<Tweet>>()
        .use("values", Tweet.class).deserialize(json);
    }

    public static String toJsonArray(Collection<Tweet> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<Tweet> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

    public static Tweet fromJsonToTweet(String json) {
        return new JSONDeserializer<Tweet>()
        .use(null, Tweet.class).deserialize(json);
    }

    public String getAuthorUsername() {
        return this.author.getUsername();
    }

    public static String toJsonArrayWithoutMTUserDetails(Collection<Tweet> collection) {
        return new JSONSerializer().exclude("*.class").exclude("author").include("authorUsername").serialize(collection);
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Tweet attached = Tweet.findTweet(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

    @Transactional
    public Tweet merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Tweet merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

    public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

    public void setAuthor(MTUser author) {
        this.author = author;
    }

    public MTUser getAuthor() {
        return this.author;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
