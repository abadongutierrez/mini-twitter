package com.jabaddon.minitwitter.web.controller;

import com.jabaddon.minitwitter.domain.model.MTUser;
import com.jabaddon.minitwitter.domain.model.Tweet;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import com.jabaddon.minitwitter.domain.repository.MTUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/mtusers")
@Controller
public class MTUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MTUserController.class);

    private final MTUserRepository _mtUserRepository;

    @Autowired
    public MTUserController(MTUserRepository mtUserRepository) {
        _mtUserRepository = mtUserRepository;
    }

    @RequestMapping(value = "/{username}/timeline", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public String getUserTimeline(HttpServletRequest request, @PathVariable("username") String username) {
        Date since = null;
        List<Tweet> timeline = null;
        MTUser mtUser = _mtUserRepository.findMTUsersByUsernameEquals(username).getSingleResult();
        if (isSinceValid(request.getParameter("since"))) {
            since = new Date(Long.valueOf(request.getParameter("since")));
            LOGGER.debug("Quering timeline since [{}]", since);
            timeline =_mtUserRepository.getTimelineSince(mtUser.getId(), since);
        } else {
            LOGGER.debug("Quering timeline");
            timeline = _mtUserRepository.getTimeline(mtUser.getId());
        }
        return Tweet.toJsonArrayWithoutMTUserDetails(timeline);
    }
    
    @RequestMapping(value = "/follow/{username}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @Transactional
    public ResponseEntity<String> followUser(HttpServletRequest request, @PathVariable(value = "username") String username) {
        MTUser user = _mtUserRepository.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        MTUser userToFollow = _mtUserRepository.findMTUsersByUsernameEquals(username).getSingleResult();
        LOGGER.debug("User to follow [{}]", userToFollow);
        user.follow(userToFollow);
        _mtUserRepository.persist(user);
        _mtUserRepository.flush();
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/unfollow/{username}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @Transactional
    public ResponseEntity<String> unfollowUser(HttpServletRequest request, @PathVariable(value = "username") String username) {
        MTUser user = _mtUserRepository.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        MTUser userToUnfollow = _mtUserRepository.findMTUsersByUsernameEquals(username).getSingleResult();
        LOGGER.debug("User to unfollow [{}]", userToUnfollow);
        user.unfollow(userToUnfollow);
        _mtUserRepository.persist(user);
        _mtUserRepository.flush();
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }

    @RequestMapping(params = "find=Following", method = RequestMethod.GET)
    public String listFollowing(HttpServletRequest request, Model uiModel) {
        MTUser user = _mtUserRepository.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        uiModel.addAttribute("mtusers", user.getFollowing());
        uiModel.addAttribute("user", user);
        return "mtusers/list";
    }

    @RequestMapping(params = "find=Followers", method = RequestMethod.GET)
    public String listFollowers(HttpServletRequest request, Model uiModel) {
        MTUser user = _mtUserRepository.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
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
            uiModel.addAttribute("mtusers", _mtUserRepository.findMTUserEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) _mtUserRepository.countMTUsers() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("mtusers", _mtUserRepository.findAllMTUsers(sortFieldName, sortOrder));
        }
        MTUser user = _mtUserRepository.findMTUsersByUsernameEquals(request.getUserPrincipal().getName()).getSingleResult();
        uiModel.addAttribute("user", user);
        return "mtusers/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid MTUser mtUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, mtUser);
            return "mtusers/create";
        }
        uiModel.asMap().clear();
        _mtUserRepository.persist(mtUser);
        return "redirect:/mtusers/" + encodeUrlPathSegment(mtUser.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new MTUser());
        return "mtusers/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("mtuser_", _mtUserRepository.findMTUser(id));
        uiModel.addAttribute("itemId", id);
        return "mtusers/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid MTUser mtUser, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, mtUser);
            return "mtusers/update";
        }
        uiModel.asMap().clear();
        _mtUserRepository.merge(mtUser);
        return "redirect:/mtusers/" + encodeUrlPathSegment(mtUser.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, _mtUserRepository.findMTUser(id));
        return "mtusers/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        _mtUserRepository.remove(id);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/mtusers";
    }

    @RequestMapping(params = { "find=ByUsernameLikeOrNameLikeOrLastNameLike", "form" }, method = RequestMethod.GET)
    public String findMTUsersByUsernameLikeOrNameLikeOrLastNameLikeForm(Model uiModel) {
        return "mtusers/findMTUsersByUsernameLikeOrNameLikeOrLastNameLike";
    }

    @RequestMapping(params = "find=ByUsernameLikeOrNameLikeOrLastNameLike", method = RequestMethod.GET)
    public String findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(@RequestParam("username") String username, @RequestParam("name") String name, @RequestParam("lastName") String lastName, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("mtusers", _mtUserRepository.findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(username, name, lastName, sortFieldName, sortOrder).setFirstResult(firstResult).setMaxResults(sizeNo).getResultList());
            float nrOfPages = (float) _mtUserRepository.countFindMTUsersByUsernameLikeOrNameLikeOrLastNameLike(username, name, lastName) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("mtusers", _mtUserRepository.findMTUsersByUsernameLikeOrNameLikeOrLastNameLike(username, name, lastName, sortFieldName, sortOrder).getResultList());
        }
        return "mtusers/list";
    }

    void populateEditForm(Model uiModel, MTUser mtUser) {
        uiModel.addAttribute("mtUser", mtUser);
        uiModel.addAttribute("mtusers", _mtUserRepository.findAllMTUsers());
        uiModel.addAttribute("tweets", Tweet.findAllTweets());
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
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
