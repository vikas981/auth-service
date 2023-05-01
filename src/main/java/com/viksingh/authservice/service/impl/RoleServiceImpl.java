package com.viksingh.authservice.service.impl;


import com.viksingh.authservice.dto.request.RoleRequestDTO;
import com.viksingh.authservice.dto.response.ApiResponse;
import com.viksingh.authservice.dto.response.ResponseDTO;
import com.viksingh.authservice.exception.wrapper.RoleNotFoundException;
import com.viksingh.authservice.helper.KeycloakHelper;
import com.viksingh.authservice.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final KeycloakHelper keycloakHelper;

    public RoleServiceImpl(KeycloakHelper keycloakHelper) {
        this.keycloakHelper = keycloakHelper;
    }

    public ResponseDTO getAllRoles() {
        ClientsResource clientsResource = keycloakHelper.getClientsResource();
        if (clientsResource != null) {
            ClientRepresentation clientRep = keycloakHelper.getClientRepresentation();
            List<String> roles = clientsResource.get(clientRep.getId()).roles().list()
                    .stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());
            return ApiResponse.response(HttpStatus.OK, roles);
        }
        return ApiResponse.response(HttpStatus.BAD_REQUEST, "Roles not found");
    }

    @Override
    public ResponseEntity<ResponseDTO> makeComposite(HttpServletRequest servletRequest, String name) {
            ClientRepresentation clientRep = keycloakHelper.getClientRepresentation();
            ClientsResource clientsResource =  keycloakHelper.getClientsResource();
        if (clientRep != null && clientsResource != null) {
            RoleRepresentation role = clientsResource.get(clientRep.getId()).roles().get(name).toRepresentation();
            List<RoleRepresentation> composites = new LinkedList<>();
            composites.add(keycloakHelper.getRolesResource().get("offline_access").toRepresentation());
            keycloakHelper.addNewRole(role.getId(),composites);

            return ResponseEntity.ok(ApiResponse.response(HttpStatus.OK,"Role converted to composite role."));
        }
        return ResponseEntity.badRequest().body(ApiResponse.response(HttpStatus.BAD_REQUEST,"Something went wrong."));
    }

    public ResponseEntity<ResponseDTO> addRealmRole(HttpServletRequest servletRequest, RoleRequestDTO requestDTO) {
        try {
            List<String> roles = (List<String>) getAllRoles().getData();
            if (!roles.contains(requestDTO.getName())) {
                RoleRepresentation roleRep = new RoleRepresentation();
                roleRep.setName(requestDTO.getName());
                roleRep.setDescription("role_" + requestDTO.getName());
                ClientRepresentation clientRep = keycloakHelper.getClientRepresentation();
                ClientsResource clientsResource =  keycloakHelper.getClientsResource();
                if (clientRep != null && clientsResource != null) {
                    clientsResource.get(clientRep.getId()).roles().create(roleRep);
                }
                return ResponseEntity.ok(ApiResponse.response(HttpStatus.OK,"Role created successfully."));
            }
            return ResponseEntity.badRequest().body(ApiResponse.response(HttpStatus.BAD_REQUEST, "Role already exists."));
        } catch (Exception e) {
            throw new RoleNotFoundException(String.format("%s doesn't exists!", requestDTO.getName()),HttpStatus.BAD_REQUEST);
        }

    }
}
