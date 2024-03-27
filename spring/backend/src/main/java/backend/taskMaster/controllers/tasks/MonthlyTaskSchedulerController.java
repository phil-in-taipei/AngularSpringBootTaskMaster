package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskPostRequest;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.user.User;
import backend.taskMaster.services.tasks.MonthlyTaskSchedulingService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/monthly")
@RequiredArgsConstructor
public class MonthlyTaskSchedulerController {

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

    @Autowired
    UserDetailsServiceImplementation userService;

    @PostMapping("/create")
    public ResponseEntity<?> createMonthlyTaskScheduler(
            @RequestBody MonthlySchedulerPostRequest request,
            Authentication authentication
    ) {
        System.out.println(request.toString());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            User user = userService.loadUserByUsername(userDetails.getUsername());
            MonthlyTaskScheduler monthlyTaskScheduler =
                monthlyTaskSchedulingService
                        .saveMonthlyTaskScheduler(new MonthlyTaskScheduler(
                                request.getMonthlyTaskName(),
                                request.getDayOfMonth(),
                                user
                        ));
            monthlyTaskScheduler.generateTemplateSelectorString();
            return new ResponseEntity<>(
                    monthlyTaskScheduler,
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }


    @GetMapping("/schedulers")
    public ResponseEntity<?> getUsersMonthlyTaskSchedulers(
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            return new ResponseEntity<>(
                    monthlyTaskSchedulingService
                            .getAllUsersMonthlyTaskSchedulers(user.getUsername()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

}
