package com.viksingh.authservice.service;



import com.viksingh.authservice.dto.request.RoleRequestDTO;
import com.viksingh.authservice.dto.response.ResponseDTO;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RoleService {
  ResponseEntity<ResponseDTO> addRealmRole(HttpServletRequest servletRequest, RoleRequestDTO request);
  ResponseDTO getAllRoles();
  ResponseEntity<ResponseDTO> makeComposite(HttpServletRequest servletRequest,String name);
}
