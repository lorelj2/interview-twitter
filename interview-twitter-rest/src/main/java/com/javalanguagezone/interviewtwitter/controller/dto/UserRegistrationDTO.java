package com.javalanguagezone.interviewtwitter.controller.dto;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class UserRegistrationDTO {

  @NotBlank
  @Size(min = 1, max = 255)
  private String username;

  @NotBlank
  @Size(min = 1, max = 255)
  private String password;

  @NotBlank
  @Size(min = 1, max = 255)
  private String fullName;


}
