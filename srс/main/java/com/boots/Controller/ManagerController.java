package com.boots.Controller;

import com.boots.Entity.User;
import com.boots.Service.EmailService;
import com.boots.Service.TourFilter;
import com.boots.Service.TourService;
import com.boots.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    TourService tourService;
    @Autowired
    UserService userService;
    @Autowired
    TourFilter tourFilter;
    @Autowired
    EmailService emailService;

    User user;

    @GetMapping("")
    public String initPageManager(){
        return "manager";
    }

    @GetMapping("/addUser")
    public String registration(Model model){
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        String answer = userService.saveUser(userForm);
        if (!answer.equals("")){
            model.addAttribute("usernameError", answer);
            return "registration";
        }
        user = userForm;
        return "redirect:/";
    }
}
