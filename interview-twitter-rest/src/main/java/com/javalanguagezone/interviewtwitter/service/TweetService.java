package com.javalanguagezone.interviewtwitter.service;

import static java.util.stream.Collectors.toList;

import com.javalanguagezone.interviewtwitter.domain.Tweet;
import com.javalanguagezone.interviewtwitter.domain.User;
import com.javalanguagezone.interviewtwitter.repository.TweetRepository;
import com.javalanguagezone.interviewtwitter.service.dto.TweetDTO;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TweetService {

  private TweetRepository tweetRepository;
  private UserService userService;

  public TweetService(TweetRepository tweetRepository, UserService userService) {
    this.tweetRepository = tweetRepository;
    this.userService = userService;
  }

  public Collection<TweetDTO> followingUsersTweets(Principal principal) {
    User author = userService.getUserByName(principal.getName());
    return author.getFollowing().stream().map(this::tweetsFromUser).flatMap(Collection::stream).collect(toList());
  }

  public Collection<TweetDTO> tweetsFromUser(String username) {
    User user = userService.getUserByName(username);
    return tweetsFromUser(user);
  }

  private List<TweetDTO> tweetsFromUser(User user) {
    return tweetRepository.findAllByAuthor(user).stream().map(TweetDTO::new).collect(toList());
  }

  public TweetDTO createTweet(String tweetContent, Principal principal) {
    User user = userService.getUserByName(principal.getName());
    Tweet tweet = new Tweet(tweetContent, user);
    if(!tweet.isValid())
      throw new InvalidTweetException(tweetContent);
    Tweet saved = tweetRepository.save(tweet);
    return new TweetDTO(saved);
  }



  public static class InvalidTweetException extends RuntimeException {

    private InvalidTweetException(String tweet) {
      super("'" +  tweet + "'");
    }
  }
}
