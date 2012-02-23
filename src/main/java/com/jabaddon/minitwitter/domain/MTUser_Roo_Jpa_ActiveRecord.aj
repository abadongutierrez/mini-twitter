// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.jabaddon.minitwitter.domain;

import com.jabaddon.minitwitter.domain.MTUser;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect MTUser_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager MTUser.entityManager;
    
    public static final EntityManager MTUser.entityManager() {
        EntityManager em = new MTUser().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long MTUser.countMTUsers() {
        return entityManager().createQuery("SELECT COUNT(o) FROM MTUser o", Long.class).getSingleResult();
    }
    
    public static List<MTUser> MTUser.findAllMTUsers() {
        return entityManager().createQuery("SELECT o FROM MTUser o", MTUser.class).getResultList();
    }
    
    public static MTUser MTUser.findMTUser(Long id) {
        if (id == null) return null;
        return entityManager().find(MTUser.class, id);
    }
    
    public static List<MTUser> MTUser.findMTUserEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM MTUser o", MTUser.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void MTUser.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void MTUser.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            MTUser attached = MTUser.findMTUser(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void MTUser.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void MTUser.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public MTUser MTUser.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        MTUser merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
