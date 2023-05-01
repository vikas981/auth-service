package com.viksingh.authservice.controller;


import com.viksingh.authservice.constants.ApiUriConstants;
import com.viksingh.authservice.dto.request.RoleRequestDTO;
import com.viksingh.authservice.dto.response.ResponseDTO;
import com.viksingh.authservice.service.impl.RoleServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Log4j2
@RequestMapping(ApiUriConstants.BASE_ROLE_URL)
public class RoleController {

    private final RoleServiceImpl roleService;

    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @GetMapping(ApiUriConstants.FETCH_ALL_ROLES)
    public ResponseEntity<ResponseDTO> getAllRoles(HttpServletRequest request){
        ResponseDTO response = roleService.getAllRoles();
        return ResponseEntity.ok(response);
    }

    @PostMapping(ApiUriConstants.CREATE_ROLE)
    public ResponseEntity<ResponseDTO> addRealmRole(HttpServletRequest servletRequest, @RequestBody @NotNull RoleRequestDTO request) throws Exception {
        return roleService.addRealmRole(servletRequest,request);
    }

    @GetMapping(ApiUriConstants.MAKE_ROLE_COMPOSITE)
    public ResponseEntity<ResponseDTO> addRealmRole(HttpServletRequest servletRequest,@PathVariable("name") String name)  {
        return roleService.makeComposite(servletRequest,name);
    }



}
