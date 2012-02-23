package com.jabaddon.minitwitter.web;

import com.jabaddon.minitwitter.domain.MTUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@RequestMapping("/register")
@Controller
public class RegisterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new MTUser());
        return "register/create";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid MTUser mtuser, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateEditForm(model, mtuser);
            return "register/create";
        }
        model.asMap().clear();
        mtuser.setEnabled(true);
        mtuser.persist();
        return "register/created";
    }

    private void populateEditForm(Model uiModel, MTUser user) {
        uiModel.addAttribute("mtuser", user);
    }
}
