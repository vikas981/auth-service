package com.viksingh.authservice.controller;

import javax.servlet.http.HttpServletRequest;

import com.viksingh.authservice.constants.ApiUriConstants;
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
@RequestMapping(ApiUriConstants.AUTH_BASE_URL)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping(ApiUriConstants.SIGN_UP)
    public ResponseDTO createUser(@RequestBody @NonNull UserRequestDTO userRequestDTO){
        return authService.createUser(userRequestDTO);
    }

    @PostMapping(ApiUriConstants.DO_LOGIN)
    public ResponseDTO doLogin(@RequestBody @NonNull LoginRequestDTO loginRequestDTO){
        return authService.doLogin(loginRequestDTO);
    }

    @PutMapping(ApiUriConstants.RESET_PASSWORD)
    public ResponseDTO resetPassword(@RequestBody @NonNull ResetPasswordDTO resetPasswordDTO, HttpServletRequest request){
        return authService.resetPassword(resetPasswordDTO,request);
    }

    @PostMapping(ApiUriConstants.UPDATE_PROFILE)
    public ResponseDTO updateUser(@RequestBody @NonNull UserRequestDTO userRequestDTO){
        return authService.updateUser(userRequestDTO);
    }
}
