package com.jabaddon.minitwitter.web.controller;

import com.jabaddon.minitwitter.domain.model.MTUser;
import com.jabaddon.minitwitter.domain.repository.MTUserRepository;
import flexjson.JSONDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("/tweets")
@Controller
public class TweetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetController.class);

    private final MTUserRepository _mtUserRepository;

    @Autowired
    public TweetController(MTUserRepository mtUserRepository) {
        _mtUserRepository = mtUserRepository;
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @Transactional
    public ResponseEntity<String> tweet(HttpServletRequest request, @RequestBody String tweetText) {
        LOGGER.debug("Json received [{}]", tweetText);
        MTUser user = _mtUserRepository.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        LOGGER.debug("User found [{}]", user);
        Map map = (Map) new JSONDeserializer().use("text", String.class).deserialize(tweetText);
        LOGGER.debug("Creating a new tweet [{}]", map.get("text"));
        user.tweet((String) map.get("text"));
        _mtUserRepository.merge(user);
        LOGGER.debug("New tweet created");
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }
}
