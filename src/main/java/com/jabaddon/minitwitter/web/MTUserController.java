package com.jabaddon.minitwitter.web;

import com.jabaddon.minitwitter.domain.MTUser;
import com.jabaddon.minitwitter.domain.Tweet;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.finder.RooWebFinder;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mtusers")
@Controller
@RooWebScaffold(path = "mtusers", formBackingObject = MTUser.class, create = true)
@RooWebFinder
public class MTUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MTUserController.class);

    @RequestMapping(value = "/{username}/timeline", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public String getUserTimeline(HttpServletRequest request, @PathVariable("username") String username) {
        Date since = null;
        List<Tweet> timeline = null;
        if (isSinceValid(request.getParameter("since"))) {
            since = new Date(Long.valueOf(request.getParameter("since")));
            LOGGER.debug("Quering timeline since [{}]", since);
            timeline = MTUser.findMTUsersByUsernameEquals(username).getSingleResult().getTimelineSince(since);
        } else {
            LOGGER.debug("Quering timeline");
            timeline = MTUser.findMTUsersByUsernameEquals(username).getSingleResult().getTimeline();
        }
        return Tweet.toJsonArrayWithoutMTUserDetails(timeline);
    }
    
    @RequestMapping(value = "/follow/{username}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @Transactional
    public ResponseEntity<String> followUser(HttpServletRequest request, @PathVariable(value = "username") String username) {
        MTUser user = MTUser.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        MTUser userToFollow = MTUser.findMTUsersByUsernameEquals(username).getSingleResult();
        LOGGER.debug("User to follow [{}]", userToFollow);
        user.follow(userToFollow);
        user.persist();
        user.flush();
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/unfollow/{username}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @Transactional
    public ResponseEntity<String> unfollowUser(HttpServletRequest request, @PathVariable(value = "username") String username) {
        MTUser user = MTUser.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        MTUser userToUnfollow = MTUser.findMTUsersByUsernameEquals(username).getSingleResult();
        LOGGER.debug("User to unfollow [{}]", userToUnfollow);
        user.unfollow(userToUnfollow);
        user.persist();
        user.flush();
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @RequestMapping(params = "find=Following", method = RequestMethod.GET)
    public String listFollowing(HttpServletRequest request, Model uiModel) {
        MTUser user = MTUser.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        uiModel.addAttribute("mtusers", user.getFollowing());
        uiModel.addAttribute("user", user);
        return "mtusers/list";
    }

    @RequestMapping(params = "find=Followers", method = RequestMethod.GET)
    public String listFollowers(HttpServletRequest request, Model uiModel) {
        MTUser user = MTUser.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        uiModel.addAttribute("mtusers", user.getFollowers());
        uiModel.addAttribute("user", user);
        return "mtusers/list";
    }

    /*@RequestMapping(params = "find=ByUsernameLikeOrNameLikeOrLastNameLike", method = RequestMethod.GET)
    public String findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("name") String name, @RequestParam("lastName") String lastName, Model uiModel) {
        MTUser user = MTUser.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        uiModel.addAttribute("mtusers", MTUser.findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(
                username, name, lastName).getResultList());
        uiModel.addAttribute("user", user);
        return "mtusers/list";
    }*/

    @RequestMapping(produces = "text/html")
    public String list(HttpServletRequest request, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("mtusers", MTUser.findMTUserEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) MTUser.countMTUsers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("mtusers", MTUser.findAllMTUsers(sortFieldName, sortOrder));
        }
        MTUser user = MTUser.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        uiModel.addAttribute("user", user);
        return "mtusers/list";
    }

    private boolean isSinceValid(String since) {
        try {
            Long.parseLong(since);
            return true;
        } catch (NumberFormatException ex) {
        }
        return false;
    }
}
