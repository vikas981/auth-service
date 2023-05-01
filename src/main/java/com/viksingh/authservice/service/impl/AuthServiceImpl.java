package com.viksingh.authservice.service.impl;


import com.viksingh.authservice.config.KeycloakClientConfig;
import com.viksingh.authservice.dto.request.LoginRequestDTO;
import com.viksingh.authservice.dto.request.ResetPasswordDTO;
import com.viksingh.authservice.dto.request.UserRequestDTO;
import com.viksingh.authservice.dto.response.ApiResponse;
import com.viksingh.authservice.dto.response.LoginResponse;
import com.viksingh.authservice.dto.response.ResponseDTO;
import com.viksingh.authservice.exception.wrapper.APIException;
import com.viksingh.authservice.helper.KeycloakHelper;
import com.viksingh.authservice.service.AuthService;
import com.viksingh.authservice.utils.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final KeycloakHelper keycloakHelper;
    private final KeycloakClientConfig keycloakClientConfig;

    public AuthServiceImpl(KeycloakHelper keycloakHelper, KeycloakClientConfig keycloakClientConfig) {
        this.keycloakHelper = keycloakHelper;
        this.keycloakClientConfig = keycloakClientConfig;
    }

    @Override
    public ResponseDTO createUser(UserRequestDTO userRequestDTO) {
        UsersResource userResource = keycloakClientConfig.getUserResource();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(userRequestDTO.getUsername());
        userRepresentation.setFirstName(userRequestDTO.getFirstName());
        userRepresentation.setLastName(userRequestDTO.getLastName());
        userRepresentation.setEmail(userRequestDTO.getEmail());
        userRepresentation.setEmailVerified(false);
        RealmResource realmResource = keycloakHelper.getRealmResource();
        try{
        Response response = userResource.create(userRepresentation);
            log.info("Response |  Status: {} | Status Info: {}", response.getStatus(), response.getMetadata());
            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                log.info("User created with userId : {}", userId);
                CredentialRepresentation passwordCred = new CredentialRepresentation();
                passwordCred.setTemporary(false);
                passwordCred.setType(CredentialRepresentation.PASSWORD);
                passwordCred.setValue(userRequestDTO.getPassword());
                UserResource user = userResource.get(userId);
                // Set password credential
                user.resetPassword(passwordCred);
                RoleRepresentation realmRoleUser = realmResource.roles().get("app-user").toRepresentation();
                user.roles().realmLevel().add(Collections.singletonList(realmRoleUser));
                return ApiResponse.response(HttpStatus.OK,"User Created successfully.");
            } else if (response.getStatus() == 409) {
                return ApiResponse.response(HttpStatus.CONFLICT,"User already registered.");
            }
        }catch (Exception e) {
            log.error("Exception Occurred", e);
        }
        throw new APIException(HttpStatus.BAD_REQUEST,"User not created.");
    }


    @Override
    public ResponseDTO doLogin(LoginRequestDTO request) {
        try {
            Map<String, Object> clientCredentials = new HashMap<>();
            clientCredentials.put("secret", keycloakClientConfig.getSecretKey());
            clientCredentials.put("grant_type", "password");
            Configuration configuration =
                    new Configuration(keycloakClientConfig.getAuthUrl(), keycloakClientConfig.getRealm(),
                            keycloakClientConfig.getClientId(), clientCredentials, null);
            AuthzClient authzClient = AuthzClient.create(configuration);
            AccessTokenResponse response =
                    authzClient.obtainAccessToken(request.getUsername(), request.getPassword());
            LoginResponse.LoginResponseBuilder loginResponse = LoginResponse.builder();
            loginResponse.accessToken(response.getToken());
            loginResponse.expiresIn(response.getExpiresIn());
            loginResponse.refreshExpiresIn(response.getRefreshExpiresIn());
            loginResponse.refreshToken(response.getRefreshToken());
            loginResponse.tokenType(response.getTokenType());
            loginResponse.sessionState(response.getSessionState());
            loginResponse.scope(response.getScope());
            return ApiResponse.response(HttpStatus.OK,"Login successfully.",loginResponse.build());
        }catch (Exception e){
            log.error("Invalid request body",e);
            throw new APIException(HttpStatus.BAD_REQUEST,"Bad Request");
        }
    }


    @Override
    public ResponseDTO updateUser(UserRequestDTO userRequestDTO) {
        UsersResource usersResource = keycloakClientConfig.getUserResource();
        try{
            List<UserRepresentation> userRepresentations = keycloakHelper.getUsersResource().search(userRequestDTO.getUsername(),true);
            int size = userRepresentations.size();
            if(size > 0){
                UserRepresentation userRepresentation = userRepresentations.get(0);
                userRepresentation.setEmail(userRequestDTO.getEmail());
                userRepresentation.setFirstName(userRequestDTO.getFirstName());
                userRepresentation.setLastName(userRequestDTO.getLastName());
                usersResource.get(userRepresentation.getId()).update(userRepresentation);
                return ApiResponse.response(HttpStatus.OK,"User detail updated successfully.");
            }
            return ApiResponse.response(HttpStatus.BAD_REQUEST,"Multiple users found.");
        }catch (Exception e){
            log.error("Unable to update user details",e);
            throw new APIException(HttpStatus.BAD_REQUEST,"User detail not updated.");
        }
    }

    @Override
    public ResponseDTO resetPassword(ResetPasswordDTO resetPasswordDTO, HttpServletRequest request) {
        String emailFromJwtToken = CommonUtility.getEmail(request.getHeader(HttpHeaders.AUTHORIZATION));
        log.info("Email from JWT token : {}", emailFromJwtToken);
            try {
                UsersResource userResource = keycloakClientConfig.getUserResource();
                UserRepresentation userRepresentation =
                        keycloakHelper.getUsersResource()
                        .search(resetPasswordDTO.getUsername(), true).stream().findFirst()
                        .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST,
                            "User doesn't exists"));
                String existingEmail = userRepresentation.getEmail();
                log.info("User registered email : {}", existingEmail);
                if(emailFromJwtToken.equals(existingEmail)){
                    userResource.get(userRepresentation.getId())
                        .executeActionsEmail(List.of("UPDATE_PASSWORD"));
                    return ApiResponse.response(HttpStatus.OK, "Email sent successfully.");
                }
                else {
                    return ApiResponse.response(HttpStatus.UNAUTHORIZED, "You have not access to reset password.");
                }
            } catch (Exception e) {
                log.error("Unable to send reset password mail", e);
                throw new APIException(HttpStatus.BAD_REQUEST, "Unable to send reset password mail.");
            }
    }
}
