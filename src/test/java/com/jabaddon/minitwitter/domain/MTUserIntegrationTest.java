package com.jabaddon.minitwitter.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
public class MTUserIntegrationTest {

    @Test
    @Transactional
    public void testCountUsers() {
        createNPersistDefaultUser("xxx_890");
        assertTrue(MTUser.countMTUsers() > 0);
    }

    @Test
    @Transactional
    public void testPersistUser() {
        MTUser newUser = createNPersistDefaultUser("ado_456");
        MTUser dbUser = MTUser.findMTUser(newUser.getId());
        assertThat(newUser.getUsername(), is(dbUser.getUsername()));
    }

    @Test
    @Transactional
    public void testTweet() {
        MTUser user = createNPersistDefaultUser("hola666");
        String msg = "Hola Mundo!";
        user.tweet(msg);
        user.merge();
        user.flush();

        assertTrue(MTUser.findMTUser(user.getId()).getTweets().size() > 0);
        Tweet t = MTUser.findMTUser(user.getId()).getTweets().get(0);
        assertThat(msg, is(t.getText()));
    }

    @Test
    @Transactional
    public void testFollow() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").buildNPersist();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").buildNPersist();
        
        user1.follow(user2);
        user1.merge();
        user1.flush();

        MTUser dbUser1 = MTUser.findMTUser(user1.getId());
        MTUser dbUser2 = MTUser.findMTUser(user2.getId());
        assertThat(dbUser1.countFollowing(), is(1));
        assertThat(dbUser2.countFollowers(), is(1));
        assertThat(dbUser1.getFollowing().get(0), is(dbUser2));
        assertThat(dbUser2.getFollowers().get(0), is(dbUser1));
    }

    @Test
    @Transactional
    public void testFollow2Users() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").buildNPersist();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").buildNPersist();
        MTUser user3 = new MTUserBuilder().withUsername("user_3").buildNPersist();

        user1.follow(user2);
        user1.follow(user3);
        user1.merge();
        user1.flush();

        MTUser dbUser1 = MTUser.findMTUser(user1.getId());
        MTUser dbUser2 = MTUser.findMTUser(user2.getId());
        MTUser dbUser3 = MTUser.findMTUser(user3.getId());
        assertThat(dbUser1.countFollowing(), is(2));
        assertThat(dbUser2.countFollowers(), is(1));
        assertThat(dbUser3.countFollowers(), is(1));

        assertThat(dbUser2.getFollowers().get(0), is(dbUser1));
        assertThat(dbUser3.getFollowers().get(0), is(dbUser1));
    }
    
    @Test
    @Transactional
    public void testUnfollow() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").buildNPersist();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").buildNPersist();
        
        user1.follow(user2);
        user1.merge();
        user1.flush();

        MTUser dbUser1 = MTUser.findMTUser(user1.getId());
        MTUser dbUser2 = MTUser.findMTUser(user2.getId());
        assertThat(dbUser1.countFollowing(), is(1));
        assertThat(dbUser2.countFollowers(), is(1));

        dbUser1.unfollow(dbUser2);
        dbUser1.merge();
        dbUser1.flush();

        dbUser1 = MTUser.findMTUser(user1.getId());
        dbUser2 = MTUser.findMTUser(user2.getId());
        assertThat(dbUser1.countFollowing(), is(0));
        assertThat(dbUser2.countFollowers(), is(0));
    }
    
    @Test
    @Transactional
    public void testTweetsTimeline() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").buildNPersist();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").buildNPersist();
        MTUser user3 = new MTUserBuilder().withUsername("user_3").buildNPersist();

        user1.follow(user2);
        user1.follow(user3);
        user1.merge();
        user1.flush();

        DateTime dt = new DateTime();
        user3.tweetOnDate("My very first tweet!", dt.minusDays(-5).toDate());
        user3.tweetOnDate("My second tweet!", dt.minusDays(-4).toDate());
        user2.tweetOnDate("Hola a todos!", dt.minusDays(-3).toDate());
        user3.tweetOnDate("My third tweet!", dt.minusDays(-2).toDate());
        user2.tweetOnDate("Nobody is there?", dt.minusDays(-1).toDate());
        user1.tweetOnDate("Hey!", dt.toDate());

        user1.merge();
        user2.merge();
        user3.merge();
        user1.flush();

        MTUser dbUser1 = MTUser.findMTUser(user1.getId());
        List<Tweet> timeline = dbUser1.getTimeline(); 
        assertThat(timeline.size(), is(6));
        assertThat(timeline.get(0).getText(), is("Hey!"));
        assertThat(timeline.get(1).getText(), is("Nobody is there?"));
        assertThat(timeline.get(2).getText(), is("My third tweet!"));
        assertThat(timeline.get(3).getText(), is("Hola a todos!"));
        assertThat(timeline.get(4).getText(), is("My second tweet!"));
        assertThat(timeline.get(5).getText(), is("My very first tweet!"));
    }

    private MTUser createNPersistDefaultUser(String username) {
        MTUser newUser = new MTUserBuilder().withUsername(username).build();
        newUser.persist();
        newUser.flush();
        return newUser;
    }
}