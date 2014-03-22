package com.jabaddon.minitwitter.web.controller;

import com.jabaddon.minitwitter.domain.model.MTUser;
import com.jabaddon.minitwitter.domain.model.MTUserBuilder;
import com.jabaddon.minitwitter.domain.repository.MTUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
public class TweetControllerIntegrationTest {

    @Autowired
    private MTUserRepository _mtUserRepository;

    @Test
    @Transactional
    public void testTweet() {
        final MTUser user = new MTUserBuilder().withUsername("mta_123").build();
        _mtUserRepository.persist(user);
        
        TweetController controller = new TweetController(_mtUserRepository);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setUserPrincipal(new Principal() {
            public String getName() {
                return user.getUsername();
            }
        });
        ResponseEntity<String> re = controller.tweet(request, "{text: 'Hey!'}");
        assertThat(re.getStatusCode(), is(HttpStatus.CREATED));
        _mtUserRepository.flush();
        
        MTUser dbUser = _mtUserRepository.findMTUser(user.getId());
        assertThat(dbUser.getTweets().size(), is(1));
        assertThat(dbUser.getTweets().get(0).getText(), is("Hey!"));
    }
}
