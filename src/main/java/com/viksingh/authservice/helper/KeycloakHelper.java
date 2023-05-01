package com.viksingh.authservice.helper;

import com.viksingh.authservice.config.KeycloakClientConfig;
import com.viksingh.authservice.constants.AuthConstant;
import com.viksingh.authservice.exception.wrapper.APIException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class KeycloakHelper {

    private final KeycloakClientConfig keycloakClientConfig;
    private final Keycloak keycloak;
    private final RestTemplate restTemplate;

    public KeycloakHelper(KeycloakClientConfig keycloakClientConfig, Keycloak keycloak, RestTemplate restTemplate) {
        this.keycloakClientConfig = keycloakClientConfig;
        this.keycloak = keycloak;
        this.restTemplate = restTemplate;
    }

    public UsersResource getUsersResource(){
       return keycloak.realm(keycloakClientConfig.getRealm()).users();
    }

    public void validateToken(String authToken) {
        String tokenValidationURL = String.format(AuthConstant.TOKEN_VALIDATION_URL,keycloakClientConfig.getAuthUrl(),keycloakClientConfig.getRealm());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION,authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(tokenValidationURL,
                    HttpMethod.GET,entity, new ParameterizedTypeReference<String>() {});
            log.info("Token validation response : {}",response);
        }catch (Exception e){
            log.info("Token verification failed");
            throw new APIException(HttpStatus.UNAUTHORIZED,"Token verification failed");
        }
    }

    public ClientsResource getClientsResource() {
       return getRealmResource().clients();
    }

    public RealmResource getRealmResource(){
        return keycloak.realm(keycloakClientConfig.getRealm());
    }

    public ClientRepresentation getClientRepresentation(){
        ClientsResource clientsResource = getClientsResource();
        ClientRepresentation clientRep = null;
        if(clientsResource != null) {
            clientRep = clientsResource.findByClientId(keycloakClientConfig.getClientId()).get(0);
        }
        return clientRep;
    }

    public RolesResource getRolesResource(){
        return keycloak.realm(keycloakClientConfig.getRealm()).roles();
    }

    public void addNewRole(String id, List<RoleRepresentation> composites) {
        keycloak.realm(keycloakClientConfig.getRealm()).rolesById().addComposites(id, composites);
    }
}
