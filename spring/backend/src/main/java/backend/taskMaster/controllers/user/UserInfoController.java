package backend.taskMaster.controllers.user;

import backend.taskMaster.exceptions.auth.RefreshTokenExpiredException;
import backend.taskMaster.exceptions.user.SaveEditedUserException;
import backend.taskMaster.exceptions.user.UserNotFoundException;
import backend.taskMaster.models.auth.TokenRefreshRequest;
import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.user.User;
import backend.taskMaster.models.user.UserEditRequest;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {


    @Autowired
    UserDetailsServiceImplementation userService;

    @GetMapping("/test")
    public ResponseEntity<String> authenticatedAdminTestRoute() {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(
                "{\"message\": \"Response from authenticated endpoint successful\"}",
                httpHeaders, HttpStatus.OK
        );
    }

    @GetMapping("/authenticated")
    public ResponseEntity<Object> authenticatedUserInfo(Authentication authentication) {
        System.out.println("...Calling the get controller method....");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(userService.loadUserByUsername(userDetails.getUsername()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        }
    }

    @PatchMapping("/edit")
    public ResponseEntity<Object> editUserInfo(
            @RequestBody UserEditRequest userEditRequest,
            Authentication authentication
    ) {
        System.out.println("...Calling the edit controller method....");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            User user = userService.loadUserByUsername(userDetails.getUsername());
            return ResponseEntity.ok(
                    userService.editUserInformation(userEditRequest, user)
            );
        } catch (UsernameNotFoundException | SaveEditedUserException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        }
    }
}
