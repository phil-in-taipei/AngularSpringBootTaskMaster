package backend.taskMaster.controllers.tasks;

import backend.taskMaster.exceptions.auth.PasswordConfirmationFailureException;
import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.TaskPostRequest;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.user.User;
import backend.taskMaster.services.tasks.TaskService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class BasicTaskController {
    @Autowired
    TaskService taskService;

    @Autowired
    UserDetailsServiceImplementation userService;

    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @RequestBody TaskPostRequest request,
            Authentication authentication
    ) {
        System.out.println("Making a request to register new user");
        System.out.println(request.toString());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            User user = userService.loadUserByUsername(userDetails.getUsername());
            LocalDate date = LocalDate.parse(request.getDate());
            SingleTask singleTask = new SingleTask(
                    request.getTaskName(),
                    date, user);
            return new ResponseEntity<>(taskService.saveTask(singleTask), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }
}
