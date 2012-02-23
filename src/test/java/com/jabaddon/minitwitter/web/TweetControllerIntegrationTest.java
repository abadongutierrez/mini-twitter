package com.jabaddon.minitwitter.web;

import com.jabaddon.minitwitter.domain.MTUser;
import com.jabaddon.minitwitter.domain.MTUserBuilder;
import com.jabaddon.minitwitter.web.TweetController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
public class TweetControllerIntegrationTest {

    @Test
    @Transactional
    public void testTweet() {
        MTUser user = new MTUserBuilder().buildNPersist();
        
        TweetController controller = new TweetController();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", user.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ResponseEntity<String> re = controller.tweet(request, "{text: 'Hey!'}");
        assertThat(re.getStatusCode(), is(HttpStatus.CREATED));
        user.flush();
        
        MTUser dbUser = MTUser.findMTUser(user.getId());
        assertThat(dbUser.getTweets().size(), is(1));
        assertThat(dbUser.getTweets().get(0).getText(), is("Hey!"));
    }
}
