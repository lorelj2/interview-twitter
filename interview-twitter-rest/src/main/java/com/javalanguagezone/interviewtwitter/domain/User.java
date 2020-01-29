package com.javalanguagezone.interviewtwitter.domain;

import static java.util.Collections.singletonList;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
@ToString(exclude = {"following", "followers"})
@EqualsAndHashCode(exclude = {"following", "followers"})
public class User implements UserDetails {

  public static final List<SimpleGrantedAuthority> AUTHORITIES = singletonList(
    new SimpleGrantedAuthority("USER"));

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(unique = true)
  private String username;

  @JsonIgnore
  @ManyToMany
  private Set<User> following = new HashSet<>();

  @JsonIgnore
  @ManyToMany(mappedBy = "following")
  private Set<User> followers = new HashSet<>();

  @JsonIgnore
  private String password;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public void addFollowing(User... users) {
    following.addAll(Arrays.asList(users));
  }

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return AUTHORITIES;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public boolean isValid() {
    return username != null && username.length() <= 255 && username.length() > 0 && password != null
      && password.length() <= 255 && password.length() > 0;
  }
}
