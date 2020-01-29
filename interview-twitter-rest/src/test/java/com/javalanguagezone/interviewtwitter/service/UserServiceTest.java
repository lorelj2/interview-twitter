package com.javalanguagezone.interviewtwitter.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.javalanguagezone.interviewtwitter.service.UserService.UnknownUsernameException;
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
  public void fetchingUserProfileUnknownUsername_UsernameNotFoundExceptionThrown() {
    userService.getUserProfile("unknownUser");
  }
}
