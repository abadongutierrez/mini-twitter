package com.jabaddon.minitwitter.domain;

import com.jabaddon.minitwitter.domain.model.MTUser;
import com.jabaddon.minitwitter.domain.model.MTUserBuilder;
import com.jabaddon.minitwitter.domain.model.Tweet;
import com.jabaddon.minitwitter.domain.repository.MTUserRepository;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
public class MTUserIntegrationTest {

    @Autowired
    private MTUserRepository _mtUserRepository;

    @Test
    @Transactional
    public void testCountUsers() {
        createNPersistDefaultUser("xxx_890");
        assertTrue(_mtUserRepository.countMTUsers() > 0);
    }

    @Test
    @Transactional
    public void testPersistUser() {
        MTUser newUser = createNPersistDefaultUser("ado_456");
        MTUser dbUser = _mtUserRepository.findMTUser(newUser.getId());
        assertThat(newUser.getUsername(), is(dbUser.getUsername()));
    }

    @Test
    @Transactional
    public void testTweet() {
        MTUser user = createNPersistDefaultUser("hola666");
        String msg = "Hola Mundo!";
        user.tweet(msg);
        _mtUserRepository.merge(user);
        _mtUserRepository.flush();

        assertTrue(_mtUserRepository.findMTUser(user.getId()).getTweets().size() > 0);
        Tweet t = _mtUserRepository.findMTUser(user.getId()).getTweets().get(0);
        assertThat(msg, is(t.getText()));
    }

    @Test
    @Transactional
    public void testFollow() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").build();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").build();
        _mtUserRepository.persist(user1);
        _mtUserRepository.persist(user2);
        
        user1.follow(user2);
        _mtUserRepository.merge(user1);
        _mtUserRepository.flush();

        MTUser dbUser1 = _mtUserRepository.findMTUser(user1.getId());
        MTUser dbUser2 = _mtUserRepository.findMTUser(user2.getId());
        assertThat(dbUser1.countFollowing(), is(1));
        assertThat(dbUser2.countFollowers(), is(1));
        assertThat(dbUser1.getFollowing().get(0), is(dbUser2));
        assertThat(dbUser2.getFollowers().get(0), is(dbUser1));
    }

    @Test
    @Transactional
    public void testFollow2Users() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").build();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").build();
        MTUser user3 = new MTUserBuilder().withUsername("user_3").build();
        _mtUserRepository.persist(user1);
        _mtUserRepository.persist(user2);
        _mtUserRepository.persist(user3);

        user1.follow(user2);
        user1.follow(user3);
        _mtUserRepository.merge(user1);
        _mtUserRepository.flush();

        MTUser dbUser1 = _mtUserRepository.findMTUser(user1.getId());
        MTUser dbUser2 = _mtUserRepository.findMTUser(user2.getId());
        MTUser dbUser3 = _mtUserRepository.findMTUser(user3.getId());
        assertThat(dbUser1.countFollowing(), is(2));
        assertThat(dbUser2.countFollowers(), is(1));
        assertThat(dbUser3.countFollowers(), is(1));

        assertThat(dbUser2.getFollowers().get(0), is(dbUser1));
        assertThat(dbUser3.getFollowers().get(0), is(dbUser1));
    }
    
    @Test
    @Transactional
    public void testUnfollow() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").build();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").build();
        _mtUserRepository.persist(user1);
        _mtUserRepository.persist(user2);
        
        user1.follow(user2);
        _mtUserRepository.merge(user1);
        _mtUserRepository.flush();

        MTUser dbUser1 = _mtUserRepository.findMTUser(user1.getId());
        MTUser dbUser2 = _mtUserRepository.findMTUser(user2.getId());
        assertThat(dbUser1.countFollowing(), is(1));
        assertThat(dbUser2.countFollowers(), is(1));

        dbUser1.unfollow(dbUser2);
        _mtUserRepository.merge(dbUser1);
        _mtUserRepository.flush();

        dbUser1 = _mtUserRepository.findMTUser(user1.getId());
        dbUser2 = _mtUserRepository.findMTUser(user2.getId());
        assertThat(dbUser1.countFollowing(), is(0));
        assertThat(dbUser2.countFollowers(), is(0));
    }
    
    @Test
    @Transactional
    public void testTweetsTimeline() {
        MTUser user1 = new MTUserBuilder().withUsername("user_1").build();
        MTUser user2 = new MTUserBuilder().withUsername("user_2").build();
        MTUser user3 = new MTUserBuilder().withUsername("user_3").build();
        _mtUserRepository.persist(user1);
        _mtUserRepository.persist(user2);
        _mtUserRepository.persist(user3);

        user1.follow(user2);
        user1.follow(user3);
        _mtUserRepository.merge(user1);
        _mtUserRepository.flush();

        DateTime dt = new DateTime();
        user3.tweetOnDate("My very first tweet!", dt.minusDays(-5).toDate());
        user3.tweetOnDate("My second tweet!", dt.minusDays(-4).toDate());
        user2.tweetOnDate("Hola a todos!", dt.minusDays(-3).toDate());
        user3.tweetOnDate("My third tweet!", dt.minusDays(-2).toDate());
        user2.tweetOnDate("Nobody is there?", dt.minusDays(-1).toDate());
        user1.tweetOnDate("Hey!", dt.toDate());

        _mtUserRepository.merge(user1);
        _mtUserRepository.merge(user2);
        _mtUserRepository.merge(user3);
        _mtUserRepository.flush();

        MTUser dbUser1 = _mtUserRepository.findMTUser(user1.getId());
        List<Tweet> timeline = _mtUserRepository.getTimeline(dbUser1.getId());
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
        _mtUserRepository.persist(newUser);
        _mtUserRepository.flush();
        return newUser;
    }
}