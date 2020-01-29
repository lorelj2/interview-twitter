package com.javalanguagezone.interviewtwitter.controller;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.javalanguagezone.interviewtwitter.controller.dto.ErrorMessage;
import com.javalanguagezone.interviewtwitter.controller.dto.UserRegistrationDTO;
import com.javalanguagezone.interviewtwitter.service.UserService;
import com.javalanguagezone.interviewtwitter.service.UserService.UnknownUsernameException;
import com.javalanguagezone.interviewtwitter.service.UserService.UserAlreadyRegisteredException;
import com.javalanguagezone.interviewtwitter.service.dto.UserDTO;
import com.javalanguagezone.interviewtwitter.service.dto.UserProfileDTO;
import java.security.Principal;
import java.util.Collection;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/followers")
  public Collection<UserDTO> followers(Principal principal) {
    return userService.getUsersFollowers(principal);
  }

  @GetMapping("/following")
  public Collection<UserDTO> following(Principal principal) {
    return userService.getUsersFollowing(principal);
  }

  @GetMapping("/profile/{username}")
  public UserProfileDTO userProfile(@PathVariable String username) {
    return userService.getUserProfile(username);
  }


  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@RequestBody @Valid UserRegistrationDTO userRegistration) {
    userService.registerUser(userRegistration);
  }

  @ExceptionHandler
  @ResponseStatus(NOT_FOUND)
  public ErrorMessage handleUnknownUsernameException(UnknownUsernameException e) {
    log.warn("", e);
    return new ErrorMessage(String.format("Unknown user '%s'", e.getUsername()));
  }

  @ExceptionHandler
  @ResponseStatus(CONFLICT)
  public ErrorMessage handleUsernameExistsException(UserAlreadyRegisteredException e) {
    log.warn("", e);
    return new ErrorMessage(String.format("User already registered '%s'", e.getUsername()));
  }
}
