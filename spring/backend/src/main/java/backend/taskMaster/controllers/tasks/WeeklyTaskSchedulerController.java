package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.*;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterly;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/weekly")
@RequiredArgsConstructor
public class WeeklyTaskSchedulerController {

    @Autowired
    UserDetailsServiceImplementation userService;

    @Autowired
    WeeklyTaskSchedulingService weeklyTaskSchedulingService;

    @GetMapping("/applied-quarterly")
    public ResponseEntity<?> getUsersWeeklyTaskSchedulersAppliedQuarterly(
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            List<WeeklyTaskAppliedQuarterly> entities = weeklyTaskSchedulingService
                    .getAllUsersWeeklyTasksAppliedQuarterly(
                            user.getUsername()
                    );
            List<WeeklyTaskAppliedQuarterlyDTO> responseData = new ArrayList<>();
            for (WeeklyTaskAppliedQuarterly entity : entities) {
                responseData.add(new WeeklyTaskAppliedQuarterlyDTO(
                        entity.getId(),
                        entity.getQuarter(),
                        entity.getYear(),
                        entity.getWeeklyTaskScheduler().getId()));
            }
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    @GetMapping("/applied-quarterly/{quarter}/{year}")
    public ResponseEntity<?>
        getUsersWeeklyTaskSchedulersAppliedQuarterlyByQuarterAndYear(
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") int year,
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            List<WeeklyTaskAppliedQuarterly> entities = weeklyTaskSchedulingService
                    .getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                            QuarterlySchedulingEnum.valueOf(
                                    quarter),
                            year,
                            user.getUsername()
                    );
            List<WeeklyTaskAppliedQuarterlyDTO> responseData = new ArrayList<>();
            for (WeeklyTaskAppliedQuarterly entity : entities) {
                responseData.add(new WeeklyTaskAppliedQuarterlyDTO(
                        entity.getId(),
                        entity.getQuarter(),
                        entity.getYear(),
                        entity.getWeeklyTaskScheduler().getId()));
            }
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    @PostMapping("/apply-quarterly/{quarter}/{year}")
    public ResponseEntity<?> saveNewQuarterlyWeeklyTask(
            @RequestBody RecurringTaskAppliedQuarterlyPostRequest request,
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") int year
    ) {
        try {
            WeeklyTaskScheduler weeklyTaskScheduler = weeklyTaskSchedulingService.getWeeklyTaskScheduler(
                    request.getRecurringTaskSchedulerId()
            );
            WeeklyTaskAppliedQuarterly qWeeklyTask = new WeeklyTaskAppliedQuarterly();
            qWeeklyTask.setWeeklyTaskScheduler(weeklyTaskScheduler);
            qWeeklyTask.setQuarter(QuarterlySchedulingEnum.valueOf(quarter));
            qWeeklyTask.setYear(year);
            return new ResponseEntity<>(
                    weeklyTaskSchedulingService
                            .saveWeeklyTaskAppliedQuarterly(qWeeklyTask),
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
    public ResponseEntity<?> createWeeklyTaskScheduler(
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

    @RequestMapping(
            value="/delete/{taskId}", method=RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteWeeklyTaskScheduler(
            @PathVariable(name = "taskId") Long taskId
    ) {
        if (weeklyTaskSchedulingService
                .getWeeklyTaskScheduler(taskId) == null
        ) {
            return new ResponseEntity<>(
                    new ApiError("Deletion Failed. Item not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        weeklyTaskSchedulingService.deleteWeeklyTaskScheduler(taskId);
        return new ResponseEntity<>(
                new TaskDeletionResponse(
                        taskId,
                        "Weekly task scheduler successfully deleted!"
                )
                , HttpStatus.OK
        );
    }

    @RequestMapping(
            value="/delete-applied-quarterly/{taskId}",
            method=RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteWeeklyTaskAppliedQuarterly(
            @PathVariable(name = "taskId") Long taskId
    ) {
        if (weeklyTaskSchedulingService
                .getWeeklyTaskAppliedQuarterly(taskId) == null
        ) {
            return new ResponseEntity<>(
                    new ApiError("Deletion Failed. Item not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        weeklyTaskSchedulingService
                .deleteWeeklyTaskAppliedQuarterly(taskId);
        return new ResponseEntity<>(
                new TaskDeletionResponse(
                        taskId, "Weekly task application successfully deleted!"
                )
                , HttpStatus.OK
        );
    }


    @GetMapping("/schedulers")
    public ResponseEntity<?> getUsersWeeklyTaskSchedulers(
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
