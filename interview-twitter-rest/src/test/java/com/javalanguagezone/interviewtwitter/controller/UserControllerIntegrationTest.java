package com.javalanguagezone.interviewtwitter.controller;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import com.javalanguagezone.interviewtwitter.controller.dto.ErrorMessage;
import com.javalanguagezone.interviewtwitter.controller.dto.UserRegistrationDTO;
import com.javalanguagezone.interviewtwitter.service.dto.UserDTO;
import com.javalanguagezone.interviewtwitter.service.dto.UserProfileDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserControllerIntegrationTest extends RestIntegrationTest {

  @Test
  public void followersRequested_allFollowersReturned() {
    ResponseEntity<UserDTO[]> response = withAuthTestRestTemplate()
      .getForEntity("/followers", UserDTO[].class);
    assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    List<UserDTO> followers = Arrays.asList(response.getBody());
    assertThat(followers, hasSize(1));
    assertThat(extractUsernames(followers), contains("rogerkver"));
  }

  @Test
  public void getFollowingFromFirstPage() {
    ResponseEntity<UserDTO[]> response = withAuthTestRestTemplate()
      .getForEntity("/following", UserDTO[].class);
    assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    List<UserDTO> following = Arrays.asList(response.getBody());
    assertThat(following, hasSize(4));
    assertThat(extractUsernames(following), containsInAnyOrder(followingUsers()));
  }

  @Test
  public void userProfileForValidUsername_userProfileReturned() {
    ResponseEntity<UserProfileDTO> response = withAuthTestRestTemplate()
      .getForEntity("/profile/{username}",
        UserProfileDTO.class, Collections.singletonMap("username", "satoshiNakamoto"));
    assertThat(response.getStatusCode().is2xxSuccessful(), is(true));

    UserProfileDTO userProfile = response.getBody();
    assertThat(userProfile, notNullValue());
    assertThat(userProfile.getFollowersCount(), is(4L));
    assertThat(userProfile.getFollowingCount(), is(0L));
    assertThat(userProfile.getPostCount(), is(2L));
  }

  @Test
  public void userProfileForInvalidUsername_userProfileReturned() {
    ResponseEntity<ErrorMessage> response = withAuthTestRestTemplate()
      .getForEntity("/profile/{username}",
        ErrorMessage.class, Collections.singletonMap("username", "unknown"));

    assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    ErrorMessage errorMessage = response.getBody();
    assertThat(errorMessage.getMessage(), containsString("unknown"));
  }


  @Test
  public void registerUserRequest_200OkReturned() {
    UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
    userRegistrationDTO.setUsername("mmaric");
    userRegistrationDTO.setPassword("password");

    ResponseEntity<String> response = withAuthTestRestTemplate()
      .postForEntity("/register", userRegistrationDTO, String.class);

    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  public void registerUserRequestWithInvalidData_400Returned() {

    UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
    userRegistrationDTO.setUsername("satoshiNakamoto");
    userRegistrationDTO.setPassword("password");

    ResponseEntity<ErrorMessage> response = withAuthTestRestTemplate()
      .postForEntity("/register", userRegistrationDTO, ErrorMessage.class);

    assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
    ErrorMessage body = response.getBody();

    assertThat(body.getMessage(), not(isEmptyString()));
  }

  @Test
  public void registerUserRequestWithAlreadyRegisteredUsername_409Returned() {

    UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
    userRegistrationDTO.setUsername("");
    userRegistrationDTO.setPassword("");

    ResponseEntity<ErrorMessage> response = withAuthTestRestTemplate()
      .postForEntity("/register", userRegistrationDTO, ErrorMessage.class);

    ErrorMessage body = response.getBody();

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(body.getMessage(), not(isEmptyString()));
  }

  private List<String> extractUsernames(List<UserDTO> users) {
    return users.stream().map(UserDTO::getUsername).collect(toList());
  }
}
