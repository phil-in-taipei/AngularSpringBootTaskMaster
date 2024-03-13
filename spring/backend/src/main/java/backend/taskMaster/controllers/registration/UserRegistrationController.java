package backend.taskMaster.controllers.registration;

import backend.taskMaster.exceptions.auth.PasswordConfirmationFailureException;
import backend.taskMaster.models.registration.RegisterRequest;
import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.services.registration.UserRegistrationService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class UserRegistrationController {

    @Autowired
    UserRegistrationService userRegistrationService;

    @PostMapping("/user")
    //@CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest request
    ) {
        System.out.println("Making a request to register new user");
        System.out.println(request.toString());
        try {
            return new ResponseEntity<>(userRegistrationService.register(request), HttpStatus.CREATED);
        } catch (PasswordConfirmationFailureException  e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("A user with that username already exists. Please try again")
            );
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(
            @RequestBody RegisterRequest request
    ) {
        try {
            return new ResponseEntity<>(userRegistrationService.registerAdmin(request), HttpStatus.CREATED);
        } catch (PasswordConfirmationFailureException  e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("A user with that username already exists. Please try again")
            );
        }
    }
}
