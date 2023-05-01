package com.viksingh.authservice.controller;

import javax.servlet.http.HttpServletRequest;

import com.viksingh.authservice.dto.request.LoginRequestDTO;
import com.viksingh.authservice.dto.request.ResetPasswordDTO;
import com.viksingh.authservice.dto.request.UserRequestDTO;
import com.viksingh.authservice.dto.response.ResponseDTO;
import com.viksingh.authservice.service.AuthService;
import com.viksingh.authservice.service.impl.AuthServiceImpl;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/sign_up")
    public ResponseDTO createUser(@RequestBody @NonNull UserRequestDTO userRequestDTO){
        return authService.createUser(userRequestDTO);
    }

    @PostMapping("/do_login")
    public ResponseDTO doLogin(@RequestBody @NonNull LoginRequestDTO loginRequestDTO){
        return authService.doLogin(loginRequestDTO);
    }

    @PutMapping("/reset_password")
    public ResponseDTO resetPassword(@RequestBody @NonNull ResetPasswordDTO resetPasswordDTO, HttpServletRequest request){
        return authService.resetPassword(resetPasswordDTO,request);
    }

    @PostMapping("/update_profile")
    public ResponseDTO updateUser(@RequestBody @NonNull UserRequestDTO userRequestDTO){
        return authService.updateUser(userRequestDTO);
    }
}
