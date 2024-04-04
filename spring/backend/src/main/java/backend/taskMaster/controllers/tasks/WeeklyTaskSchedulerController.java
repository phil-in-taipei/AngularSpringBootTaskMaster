package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.apiData.WeeklySchedulerPostRequest;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.tasks.taskSchedulers.WeeklyTaskScheduler;
import backend.taskMaster.models.user.User;
import backend.taskMaster.services.tasks.WeeklyTaskSchedulingService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weekly")
@RequiredArgsConstructor
public class WeeklyTaskSchedulerController {

    @Autowired
    UserDetailsServiceImplementation userService;

    @Autowired
    WeeklyTaskSchedulingService weeklyTaskSchedulingService;


    @PostMapping("/create")
    public ResponseEntity<?> createMonthlyTaskScheduler(
            @RequestBody WeeklySchedulerPostRequest request,
            Authentication authentication
    ) {
        System.out.println(request.toString());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            User user = userService.loadUserByUsername(
                    userDetails.getUsername()
            );
            WeeklyTaskScheduler monthlyTaskScheduler =
                    weeklyTaskSchedulingService
                            .saveWeeklyTaskScheduler(
                                    new WeeklyTaskScheduler(
                                            request.getWeeklyTaskName(),
                                            request.getDayOfWeek(),
                                            user
                                    )
                            );
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
                    weeklyTaskSchedulingService
                            .getAllUsersWeeklyTaskSchedulers(user.getUsername()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }


}