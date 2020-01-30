package com.javalanguagezone.interviewtwitter.service.dto;

import static lombok.AccessLevel.PRIVATE;

import com.javalanguagezone.interviewtwitter.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class UserProfileDTO {

  private Long id;
  private String username;
  private String fullName;
  private long followersCount;
  private long followingCount;
  private long postCount;

  public UserProfileDTO(User user, long followersCount, long followingCount, long postCount) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.fullName = user.getFullName();
    this.followersCount = followersCount;
    this.followingCount = followingCount;
    this.postCount = postCount;
  }
}
