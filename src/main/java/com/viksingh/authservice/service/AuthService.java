package com.viksingh.authservice.service;


import com.viksingh.authservice.dto.request.LoginRequestDTO;
import com.viksingh.authservice.dto.request.ResetPasswordDTO;
import com.viksingh.authservice.dto.request.UserRequestDTO;
import com.viksingh.authservice.dto.response.ResponseDTO;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
  ResponseDTO createUser(UserRequestDTO userRequestDTO);
  ResponseDTO doLogin(LoginRequestDTO request);
  ResponseDTO resetPassword(ResetPasswordDTO resetPasswordDTO, HttpServletRequest request);
  ResponseDTO updateUser(UserRequestDTO userRequestDTO);

}
