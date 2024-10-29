package com._INFINI.PI.controllers;

import com._INFINI.PI.config.LogoutService;
import com._INFINI.PI.entities.AuthenticationRequest;
import com._INFINI.PI.entities.AuthenticationResponse;
import com._INFINI.PI.entities.RegisterRequest;
import com._INFINI.PI.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {

  private final AuthenticationService service;
  private final LogoutService logoutService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@Valid
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody AuthenticationRequest request
  ) {


    return ResponseEntity.ok(service.authenticate(request, "Variables valides"));
  }


  @PostMapping("/login")
  public ResponseEntity<String> login(HttpServletRequest request, Authentication authentication) {
    service.login(request, authentication);
    return ResponseEntity.ok("User logged in successfully");
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.logout(request, response);
    return ResponseEntity.ok("User logged out successfully");
  }
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  /*@PostMapping("/logout")
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    logoutService.logout(request, response, authentication);
  }*/
}
