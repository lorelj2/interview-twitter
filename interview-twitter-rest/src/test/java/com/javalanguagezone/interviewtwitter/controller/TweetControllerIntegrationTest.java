package com.javalanguagezone.interviewtwitter.controller;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.javalanguagezone.interviewtwitter.controller.dto.ErrorMessage;
import com.javalanguagezone.interviewtwitter.domain.Tweet;
import com.javalanguagezone.interviewtwitter.repository.TweetRepository;
import com.javalanguagezone.interviewtwitter.service.dto.TweetDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TweetControllerIntegrationTest extends RestIntegrationTest {

  @Autowired
  private TweetRepository tweetRepository;

  @Test
  public void tooLongTweetCreated_BadRequestReceived() {
    String request = IntStream.rangeClosed(1, 141).mapToObj(String::valueOf).collect(joining());
    ResponseEntity<TweetDTO> response = doCreateTweetRequest(request);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
  }

  private ResponseEntity<TweetDTO> doCreateTweetRequest(String tweetContent) {
   return withAuthTestRestTemplate().postForEntity("/tweets", tweetContent, TweetDTO.class);
  }

  @Test
  public void createTweet_tweetIsSaved() {
    ResponseEntity<TweetDTO> response = doCreateTweetRequest("Tweet");
    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    TweetDTO tweet = response.getBody();
    Tweet fromDb = tweetRepository.findOne(tweet.getId());
    assertThat(fromDb, notNullValue());
    assertThat(fromDb.getContent(), equalTo(tweet.getContent()));
    assertThat(fromDb.getAuthor().getUsername(), equalTo(tweet.getAuthor()));
    assertThat(fromDb.getAuthor().getFullName(), equalTo(tweet.getAuthorFullName()));
  }

  @Test
  public void tweetsFromFollowing_onlyFollowingAuthorsTweetsAreReturned() {
    ResponseEntity<TweetDTO[]> response = withAuthTestRestTemplate().getForEntity("/tweets", TweetDTO[].class);
    assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    List<String> authors = extractAuthorNames(response.getBody());
    assertThat(authors, hasSize(followingUsers().length));
    assertThat(authors, containsInAnyOrder(followingUsers()));
  }

  @Test
  public void tweetsFromUser_onlyThatUserTweetsAreReturned() {
    ResponseEntity<TweetDTO[]> response = withAuthTestRestTemplate().getForEntity("/tweets/{username}",
      TweetDTO[].class, Collections.singletonMap("username", getUsernameOfAuthUser()));
    assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    List<String> authors = extractAuthorNames(response.getBody());
    assertThat(authors, hasSize(1));
    assertThat(authors, contains(getUsernameOfAuthUser()));
  }

@Test
  public void tweetsFromUserWithWrongUsername_badRequestReturned() {
    ResponseEntity<ErrorMessage> response = withAuthTestRestTemplate().getForEntity("/tweets/{username}",
      ErrorMessage.class, Collections.singletonMap("username", "unknown"));
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getMessage(), containsString("unknown"));
  }

  private List<String> extractAuthorNames(TweetDTO[] body) {
    return Arrays.stream(body).map(TweetDTO::getAuthor).distinct().collect(toList());
  }
}
