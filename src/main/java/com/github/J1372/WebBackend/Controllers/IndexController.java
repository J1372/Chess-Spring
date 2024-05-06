package com.github.J1372.WebBackend.Controllers;

import com.github.J1372.WebBackend.Config.UserSession;
import com.github.J1372.WebBackend.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

    private UserDetailsService userDetailsService;
    private UserService userService;

    public IndexController(UserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @GetMapping(value = { "", "/", "/home", "/users/*", "games/*", "/login", "/create-account" })
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/username")
    @ResponseBody
    public ResponseEntity<String> getUsername(@AuthenticationPrincipal UserSession user) {
        String username = user == null ? "" : user.getUsername();
        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    @Validated
    @Getter
    @Setter
    static class LoginDto {
        @NotEmpty
        String user;
        @NotEmpty
        String pass;
    }

    @PostMapping("/login/process")
    @ResponseBody
    public ResponseEntity<String> loginProcess(HttpServletRequest request, LoginDto formBody) throws ServletException {
        try {
            request.login(formBody.user, formBody.pass);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Could not login with that info.", HttpStatus.FORBIDDEN);
        }
    }

    @Validated
    @Getter
    @Setter
    static class RegistrationDto {
        @NotEmpty
        String user;
        @NotEmpty
        String pass;
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<String> addUser(HttpServletRequest request, RegistrationDto formBody) {
        String errMsg = userService.getRegistrationErrors(formBody.user, formBody.pass);

        if (!errMsg.isEmpty()) {
            return new ResponseEntity<>(errMsg, HttpStatus.FORBIDDEN);
        }

        UserService.UserRegStatus status = userService.create(formBody.user, formBody.pass);
        if (status == UserService.UserRegStatus.EXISTS) {
            return new ResponseEntity<>("User with that name already exists.", HttpStatus.FORBIDDEN);
        } else if (status != UserService.UserRegStatus.CREATED) {
            return new ResponseEntity<>("Could not create account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // creation success.

        try {
            request.login(formBody.user, formBody.pass);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ServletException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Auto-login failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
