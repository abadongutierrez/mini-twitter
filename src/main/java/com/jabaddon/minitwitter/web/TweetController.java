package com.jabaddon.minitwitter.web;

import com.jabaddon.minitwitter.domain.MTUser;
import com.jabaddon.minitwitter.domain.Tweet;
import flexjson.JSONDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagExtraInfo;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/tweets")
@Controller
public class TweetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetController.class);

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @Transactional
    public ResponseEntity<String> tweet(HttpServletRequest request, @RequestBody String tweetText) {
        LOGGER.debug("Json received [{}]", tweetText);
        MTUser user = MTUser.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        LOGGER.debug("User found [{}]", user);
        Map map = (Map) new JSONDeserializer().use("text", String.class).deserialize(tweetText);
        LOGGER.debug("Creating a new tweet [{}]", map.get("text"));
        user.tweet((String) map.get("text"));
        user.merge();
        LOGGER.debug("New tweet created");
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }
}
