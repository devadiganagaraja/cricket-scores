package edu.cricket.api.cricketscores.rest.controller;

import edu.cricket.api.cricketscores.rest.request.UserCredentials;
import edu.cricket.api.cricketscores.rest.request.UserForm;
import edu.cricket.api.cricketscores.rest.response.model.UserInfo;
import edu.cricket.api.cricketscores.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @CrossOrigin
    @PostMapping("/userSignIn")
    public UserInfo userSignIn(@RequestBody UserCredentials userCredentials) {
        return userService.getUserInfoByUserNameAndPassword(userCredentials.getUserName(), userCredentials.getPassword());
    }


    @CrossOrigin
    @PostMapping("/userSignUp")
    public UserInfo userSignUp(@RequestBody UserForm userForm) {
        return userService.registerUser(userForm.getUserName(), userForm.getPassword(), userForm.getPhone());
    }
}
