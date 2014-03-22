package com.jabaddon.minitwitter.domain.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import com.jabaddon.minitwitter.domain.model.MTUser;
import com.jabaddon.minitwitter.domain.model.Tweet;
import org.op4j.Op;
import org.op4j.functions.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MTUserRepository {
    public static final List<String> fieldNames4OrderClauseFilter =
        java.util.Arrays.asList("LOGGER", "username", "password", "passwordConfirmation", "enabled", "name", "lastName", "tweets", "following", "followers");

    private static final Logger LOGGER = LoggerFactory.getLogger(MTUserRepository.class);

    @PersistenceContext
    transient EntityManager entityManager;

    public Long countFindMTUsersByUsernameEquals(String username) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        EntityManager em = entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM MTUser AS o WHERE o.username = :username", Long.class);
        q.setParameter("username", username);
        return ((Long) q.getSingleResult());
    }

    public Long countFindMTUsersByUsernameLikeOrNameLikeOrLastNameLike(String username, String name, String lastName) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        username = username.replace('*', '%');
        if (username.charAt(0) != '%') {
            username = "%" + username;
        }
        if (username.charAt(username.length() - 1) != '%') {
            username = username + "%";
        }
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        name = name.replace('*', '%');
        if (name.charAt(0) != '%') {
            name = "%" + name;
        }
        if (name.charAt(name.length() - 1) != '%') {
            name = name + "%";
        }
        if (lastName == null || lastName.length() == 0) throw new IllegalArgumentException("The lastName argument is required");
        lastName = lastName.replace('*', '%');
        if (lastName.charAt(0) != '%') {
            lastName = "%" + lastName;
        }
        if (lastName.charAt(lastName.length() - 1) != '%') {
            lastName = lastName + "%";
        }
        EntityManager em = entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM MTUser AS o WHERE LOWER(o.username) LIKE LOWER(:username)  OR LOWER(o.name) LIKE LOWER(:name)  OR LOWER(o.lastName) LIKE LOWER(:lastName)", Long.class);
        q.setParameter("username", username);
        q.setParameter("name", name);
        q.setParameter("lastName", lastName);
        return ((Long) q.getSingleResult());
    }

    public TypedQuery<MTUser> findMTUsersByUsernameEquals(String username) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        EntityManager em = entityManager();
        TypedQuery<MTUser> q = em.createQuery("SELECT o FROM MTUser AS o WHERE o.username = :username", MTUser.class);
        q.setParameter("username", username);
        return q;
    }

    public TypedQuery<MTUser> findMTUsersByUsernameEquals(String username, String sortFieldName, String sortOrder) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        EntityManager em = entityManager();
        String jpaQuery = "SELECT o FROM MTUser AS o WHERE o.username = :username";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<MTUser> q = em.createQuery(jpaQuery, MTUser.class);
        q.setParameter("username", username);
        return q;
    }

    public TypedQuery<MTUser> findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(String username, String name, String lastName, String sortFieldName, String sortOrder) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        username = username.replace('*', '%');
        if (username.charAt(0) != '%') {
            username = "%" + username;
        }
        if (username.charAt(username.length() - 1) != '%') {
            username = username + "%";
        }
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        name = name.replace('*', '%');
        if (name.charAt(0) != '%') {
            name = "%" + name;
        }
        if (name.charAt(name.length() - 1) != '%') {
            name = name + "%";
        }
        if (lastName == null || lastName.length() == 0) throw new IllegalArgumentException("The lastName argument is required");
        lastName = lastName.replace('*', '%');
        if (lastName.charAt(0) != '%') {
            lastName = "%" + lastName;
        }
        if (lastName.charAt(lastName.length() - 1) != '%') {
            lastName = lastName + "%";
        }
        EntityManager em = entityManager();
        String jpaQuery = "SELECT o FROM MTUser AS o WHERE LOWER(o.username) LIKE LOWER(:username)  OR LOWER(o.name) LIKE LOWER(:name)  OR LOWER(o.lastName) LIKE LOWER(:lastName)";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<MTUser> q = em.createQuery(jpaQuery, MTUser.class);
        q.setParameter("username", username);
        q.setParameter("name", name);
        q.setParameter("lastName", lastName);
        return q;
    }

    public final EntityManager entityManager() {
        if (entityManager == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return entityManager;
    }

    public long countMTUsers() {
        return entityManager().createQuery("SELECT COUNT(o) FROM MTUser o", Long.class).getSingleResult();
    }

    public List<MTUser> findAllMTUsers() {
        return entityManager().createQuery("SELECT o FROM MTUser o", MTUser.class).getResultList();
    }

    public List<MTUser> findAllMTUsers(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM MTUser o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, MTUser.class).getResultList();
    }

    public MTUser findMTUser(Long id) {
        if (id == null) return null;
        return entityManager().find(MTUser.class, id);
    }

    public List<MTUser> findMTUserEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM MTUser o", MTUser.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public List<MTUser> findMTUserEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM MTUser o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, MTUser.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public List<Tweet> getTimeline(Long id) {
        MTUser user = findMTUser(id);
        List<Long> ids = Op.onList(user.getFollowing()).map(Get.attrOfLong("id")).get();
        ids.add(user.getId());
        TypedQuery<Tweet> tq = entityManager().createQuery(
            "select t from Tweet t where t.author.id in (:ids) order by t.timestamp asc", Tweet.class);
        tq.setParameter("ids", ids);
        return tq.getResultList();
    }

    public List<Tweet> getTimelineSince(Long id, Date date) {
        MTUser user = findMTUser(id);
        List<Long> ids = Op.onList(user.getFollowing()).map(Get.attrOfLong("id")).get();
        ids.add(user.getId());
        TypedQuery<Tweet> tq = entityManager().createQuery(
            "select t from Tweet t where t.author.id in (:ids) and t.timestamp > :since order by t.timestamp asc", Tweet.class);
        tq.setParameter("ids", ids);
        tq.setParameter("since", date);
        return tq.getResultList();
    }

    public boolean isFollowing(Long idUser, Long idUserFollowing) {
        MTUser user = findMTUser(idUser);
        return user.getFollowing().contains(findMTUser(idUserFollowing));
    }

    public TypedQuery<MTUser> findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(String username, String name, String lastName) {
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
        EntityManager em = entityManager();
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

    @Transactional
    public void persist(MTUser mtUser) {
        entityManager().persist(mtUser);
    }

    @Transactional
    public void remove(Long id) {
        MTUser user = findMTUser(id);
        if (user != null) {
            entityManager().remove(user);
        }
    }

    @Transactional
    public void flush() {
        entityManager().flush();
    }

    @Transactional
    public void clear() {
        entityManager().clear();
    }

    @Transactional
    public MTUser merge(MTUser toBeMerged) {
        MTUser merged = entityManager().merge(toBeMerged);
        entityManager().flush();
        return merged;
    }
}
