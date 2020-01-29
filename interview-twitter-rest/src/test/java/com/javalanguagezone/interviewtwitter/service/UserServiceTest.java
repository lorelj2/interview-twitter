package com.javalanguagezone.interviewtwitter.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.javalanguagezone.interviewtwitter.controller.dto.UserRegistrationDTO;
import com.javalanguagezone.interviewtwitter.domain.User;
import com.javalanguagezone.interviewtwitter.repository.UserRepository;
import com.javalanguagezone.interviewtwitter.service.UserService.InvalidUserException;
import com.javalanguagezone.interviewtwitter.service.UserService.UnknownUsernameException;
import com.javalanguagezone.interviewtwitter.service.UserService.UserAlreadyRegisteredException;
import com.javalanguagezone.interviewtwitter.service.dto.UserProfileDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Test(expected = UsernameNotFoundException.class)
  public void loadingUserWithUnknownUsername_UsernameNotFoundExceptionThrown() {
    userService.loadUserByUsername("unknownUser");
  }

  @Test
  public void fetchingUserProfileWithUserWithPostsAndFollowers_UserProfileReturned() {
    UserProfileDTO userProfile = userService.getUserProfile("satoshiNakamoto");

    assertThat(userProfile, notNullValue());
    assertThat(userProfile.getFollowersCount(), is(1L));
    assertThat(userProfile.getFollowingCount(), is(1L));
    assertThat(userProfile.getPostCount(), is(2L));
  }


  @Test(expected = UnknownUsernameException.class)
  public void fetchingUserProfileUnknownUsername_UnknownUsernameExceptionThrown() {
    userService.getUserProfile("unknownUser");
  }

  @Test
  public void registeringUser_UserCreated() {
    UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
    registrationDTO.setUsername("test");
    registrationDTO.setPassword("password");
    userService.registerUser(registrationDTO);

    User test = userRepository.findOneByUsername("test");
    assertThat(test, notNullValue());
    assertThat(test.getUsername(), is(registrationDTO.getUsername()));
    assertThat(test.getPassword(), is(registrationDTO.getPassword()));
  }

  @Test(expected = UserAlreadyRegisteredException.class)
  public void registeringUserThatExists_UsernameExistsExceptionThrown() {
    UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
    registrationDTO.setUsername("satoshiNakamoto");
    registrationDTO.setPassword("password");
    userService.registerUser(registrationDTO);
  }

  @Test(expected = InvalidUserException.class)
  public void registeringInvalidUser_InvalidUserExceptionThrown() {
    UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
    registrationDTO.setPassword("password");
    userService.registerUser(registrationDTO);
  }

}
