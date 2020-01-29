package com.javalanguagezone.interviewtwitter.service;

import static java.util.stream.Collectors.toList;

import com.javalanguagezone.interviewtwitter.controller.dto.UserRegistrationDTO;
import com.javalanguagezone.interviewtwitter.domain.User;
import com.javalanguagezone.interviewtwitter.repository.TweetRepository;
import com.javalanguagezone.interviewtwitter.repository.UserRepository;
import com.javalanguagezone.interviewtwitter.service.dto.UserDTO;
import com.javalanguagezone.interviewtwitter.service.dto.UserProfileDTO;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private UserRepository userRepository;

  private TweetRepository tweetRepository;

  public UserService(UserRepository userRepository, TweetRepository tweetRepository) {
    this.userRepository = userRepository;
    this.tweetRepository = tweetRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findOneByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    return user;
  }

  @Transactional
  public Collection<UserDTO> getUsersFollowing(Principal principal) {
    User user = getUserByName(principal.getName());
    return convertUsersToDTOs(user.getFollowing());
  }

  @Transactional
  public Collection<UserDTO> getUsersFollowers(Principal principal) {
    User user = getUserByName(principal.getName());
    return convertUsersToDTOs(user.getFollowers());
  }

  @Transactional
  public UserProfileDTO getUserProfile(String username) {
    User user = getUserByName(username);

    long followers = userRepository.countFollowersByUsername(username);
    long following = userRepository.countFollowingsByUsername(username);
    long postCount = tweetRepository.countByAuthor(user);

    return new UserProfileDTO(user, followers, following, postCount);
  }

  @Transactional
  public void registerUser(UserRegistrationDTO userRegistrationDTO) {

    User newUser = new User(userRegistrationDTO.getUsername(), userRegistrationDTO.getPassword());
    if (!newUser.isValid()) {
      throw new InvalidUserException("Username or password invalid");
    }

    User existingUser = userRepository.findOneByUsername(userRegistrationDTO.getUsername());
    if (existingUser != null) {
      throw new UserExistsException(existingUser.getUsername());
    }

    userRepository.save(newUser);
  }

  User getUserByName(String username) {
    User user = userRepository.findOneByUsername(username);
    if (user != null) {
      return user;
    }
    throw new UnknownUsernameException(username);
  }

  private List<UserDTO> convertUsersToDTOs(Set<User> users) {
    return users.stream().map(UserDTO::new).collect(toList());
  }

  public static class UnknownUsernameException extends RuntimeException {

    @Getter
    private String username;

    private UnknownUsernameException(String username) {
      super(username);
      this.username = username;
    }
  }

  public static class UserExistsException extends RuntimeException {

    @Getter
    private String username;

    private UserExistsException(String username) {
      super(username);
      this.username = username;
    }
  }

  public static class InvalidUserException extends RuntimeException {

    private InvalidUserException(String message) {
      super(message);
    }
  }

}
