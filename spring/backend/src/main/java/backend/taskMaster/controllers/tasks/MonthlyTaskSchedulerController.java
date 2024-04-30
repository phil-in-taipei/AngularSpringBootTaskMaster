package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.apiData.MonthlyTaskAppliedQuarterlyDTO;
import backend.taskMaster.models.tasks.apiData.RecurringTaskAppliedQuarterlyPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskDeletionResponse;
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

    @GetMapping("/applied-quarterly/{quarter}/{year}")
    public ResponseEntity<?>
        getUsersMonthlyTaskSchedulersAppliedQuarterlyByQuarterAndYear(
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") int year,
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            List<MonthlyTaskAppliedQuarterly> entities = monthlyTaskSchedulingService
                    .getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                            QuarterlySchedulingEnum.valueOf(
                                    quarter),
                            year,
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
    @PostMapping("/apply-quarterly")
    public ResponseEntity<?> saveNewQuarterlyMonthlyTask(
            @RequestBody RecurringTaskAppliedQuarterlyPostRequest request
    ) {
        try {
            System.out.println(request);
            MonthlyTaskScheduler monthlyTask = monthlyTaskSchedulingService
                    .getMonthlyTaskScheduler(
                            request.getRecurringTaskSchedulerId()
                    );
            MonthlyTaskAppliedQuarterly qMonthlyTask = new MonthlyTaskAppliedQuarterly();
            qMonthlyTask.setMonthlyTaskScheduler(monthlyTask);
            qMonthlyTask.setQuarter(QuarterlySchedulingEnum.valueOf(request.getQuarter()));
            qMonthlyTask.setYear(request.getYear());
            MonthlyTaskAppliedQuarterly entity =  monthlyTaskSchedulingService
                    .saveMonthlyTaskAppliedQuarterly(qMonthlyTask);
            return new ResponseEntity<>(
                    new MonthlyTaskAppliedQuarterlyDTO(
                            entity.getId(),
                            entity.getQuarter(),
                            entity.getYear(),
                            entity.getMonthlyTaskScheduler().getId()),
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

    @RequestMapping(
            value="/delete/{taskId}", method=RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteMonthlyTaskScheduler(
            @PathVariable(name = "taskId") Long taskId
    ) {
        if (monthlyTaskSchedulingService
                .getMonthlyTaskScheduler(taskId) == null
        ) {
            return new ResponseEntity<>(
                    new ApiError("Deletion Failed. Item not found") ,
                    HttpStatus.NOT_FOUND
            );
        }
        monthlyTaskSchedulingService.deleteMonthlyTaskScheduler(taskId);
        return new ResponseEntity<>(
                new TaskDeletionResponse(
                        taskId,
                        "Monthly task scheduler successfully deleted!"
                )
                , HttpStatus.OK);
    }

    @RequestMapping(
            value="/delete-applied-quarterly/{taskId}", method=RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteMonthlyTaskAppliedQuarterly(
            @PathVariable(name = "taskId") Long taskId
    ) {
        if (monthlyTaskSchedulingService
                .getMonthlyTaskAppliedQuarterly(taskId) == null
        ) {
            return new ResponseEntity<>(
                    new ApiError("Deletion Failed. Item not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        monthlyTaskSchedulingService
                .deleteMonthlyTaskAppliedQuarterly(taskId);
        return new ResponseEntity<>(
                new TaskDeletionResponse(
                        taskId, "Monthly task application successfully deleted!"
                )
                , HttpStatus.OK
        );
    }

    @GetMapping("/schedulers")
    public ResponseEntity<?> getUsersMonthlyTaskSchedulers(
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            List<MonthlyTaskScheduler> responseData = monthlyTaskSchedulingService
                    .getAllUsersMonthlyTaskSchedulers(user.getUsername());
            for (MonthlyTaskScheduler entity : responseData) {
                entity.generateTemplateSelectorString();
            }
            return new ResponseEntity<>(
                    responseData,
                    HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

}
