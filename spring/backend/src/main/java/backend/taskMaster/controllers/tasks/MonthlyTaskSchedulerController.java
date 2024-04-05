package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.apiData.MonthlyTaskAppliedQuarterlyDTO;
import backend.taskMaster.models.tasks.apiData.RecurringTaskAppliedQuarterlyPostRequest;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/monthly")
@RequiredArgsConstructor
public class MonthlyTaskSchedulerController {

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

    @Autowired
    UserDetailsServiceImplementation userService;

    @GetMapping("/applied-quarterly")
    public ResponseEntity<?> getUsersMonthlyTaskSchedulersAppliedQuarterly(
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            List<MonthlyTaskAppliedQuarterly> entities = monthlyTaskSchedulingService
                    .getAllUsersMonthlyTasksAppliedQuarterly(
                            user.getUsername()
                    );
            List<MonthlyTaskAppliedQuarterlyDTO> responseData = new ArrayList<>();
            for (MonthlyTaskAppliedQuarterly entity : entities) {
                responseData.add(new MonthlyTaskAppliedQuarterlyDTO(
                        entity.getId(),
                        entity.getQuarter(),
                        entity.getYear(),
                        entity.getMonthlyTaskScheduler().getId()));
            }
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    // In the form the user selects the monthly task scheduler along with the year and quarter
    // In the service method, the scheduling of the monthly tasks will be triggered for the quarter
    @PostMapping("/apply-quarterly/{quarter}/{year}")
    public ResponseEntity<?> saveNewQuarterlyMonthlyTask(
            @RequestBody RecurringTaskAppliedQuarterlyPostRequest request,
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") int year
    ) {
        try {
            MonthlyTaskScheduler monthlyTask = monthlyTaskSchedulingService
                    .getMonthlyTaskScheduler(
                            request.getRecurringTaskSchedulerId()
                    );
            MonthlyTaskAppliedQuarterly qMonthlyTask = new MonthlyTaskAppliedQuarterly();
            qMonthlyTask.setMonthlyTaskScheduler(monthlyTask);
            qMonthlyTask.setQuarter(QuarterlySchedulingEnum.valueOf(quarter));
            qMonthlyTask.setYear(year);
            return new ResponseEntity<>(
                    monthlyTaskSchedulingService
                            .saveMonthlyTaskAppliedQuarterly(qMonthlyTask),
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
