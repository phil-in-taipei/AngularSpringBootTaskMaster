package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.*;
import backend.taskMaster.models.tasks.appliedSchedulers.IntervalTaskGroupAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskScheduler;
import backend.taskMaster.models.user.User;
import backend.taskMaster.services.tasks.IntervalTaskGroupService;
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
@RequestMapping("/api/interval")
@RequiredArgsConstructor
public class IntervalTaskGroupController {

    @Autowired
    IntervalTaskGroupService intervalTaskGroupService;

    @Autowired
    UserDetailsServiceImplementation userService;


    @GetMapping("/applied-quarterly/{quarter}/{year}")
    public ResponseEntity<?>
        getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") int year,
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            List<IntervalTaskGroupAppliedQuarterly> entities = intervalTaskGroupService
                    .getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
                            QuarterlySchedulingEnum.valueOf(
                                    quarter),
                            year,
                            user.getUsername()
                    );
            List<IntervalTaskGroupAppliedQuarterlyDTO> responseData = new ArrayList<>();
            for (IntervalTaskGroupAppliedQuarterly entity : entities) {
                responseData.add(new IntervalTaskGroupAppliedQuarterlyDTO(
                        entity.getId(),
                        entity.getQuarter(),
                        entity.getYear(),
                        entity.getIntervalTaskGroup().getId()));
            }
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    // In the form the user selects the interval task group along with the year and quarter
    // In the service method, the scheduling of the interval task group member tasks will be
    // triggered for the quarter
    @PostMapping("/apply-quarterly")
    public ResponseEntity<?> saveNewQuarterlyIntervalTaskGroup(
            @RequestBody RecurringTaskAppliedQuarterlyPostRequest request
    ) {
        try {
            System.out.println(request);
            IntervalTaskGroup intervalTaskGroup = intervalTaskGroupService
                    .getIntervalTaskGroup(
                            request.getRecurringTaskSchedulerId()
                    );
            IntervalTaskGroupAppliedQuarterly iTGAQ = new IntervalTaskGroupAppliedQuarterly();
            iTGAQ.setIntervalTaskGroup(intervalTaskGroup);
            iTGAQ.setQuarter(QuarterlySchedulingEnum.valueOf(request.getQuarter()));
            iTGAQ.setYear(request.getYear());
            IntervalTaskGroupAppliedQuarterly entity =  intervalTaskGroupService
                    .saveIntervalTaskGroupAppliedQuarterly(iTGAQ);
            return new ResponseEntity<>(
                    new IntervalTaskGroupAppliedQuarterlyDTO(
                            entity.getId(),
                            entity.getQuarter(),
                            entity.getYear(),
                            entity.getIntervalTaskGroup().getId()),
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

    @PostMapping("/create-group")
    public ResponseEntity<?> createIntervalTaskGroup(
            @RequestBody IntervalTaskGroupPostRequest request,
            Authentication authentication
    ) {
        System.out.println(request.toString());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            User user = userService.loadUserByUsername(userDetails.getUsername());
            IntervalTaskGroup intervalTaskGroup =
                    intervalTaskGroupService
                            .saveIntervalTaskGroup(new IntervalTaskGroup(
                                    request.getTaskGroupName(),
                                    request.getIntervalInDays(),
                                    user
                            ));
            intervalTaskGroup.generateTemplateSelectorString();
            return new ResponseEntity<>(
                    intervalTaskGroup,
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

    @PostMapping("/create-scheduler")
    public ResponseEntity<?> createIntervalTaskScheduler(
            @RequestBody IntervalTaskSchedulerPostRequest request
    ) {
        try {
            IntervalTaskGroup intervalTaskGroup =
                    intervalTaskGroupService.getIntervalTaskGroup(
                            request.getIntervalTaskGroupId()
                    );
            List<IntervalTaskScheduler> intervalTasks = intervalTaskGroup.getIntervalTasks();
            intervalTasks.add(new IntervalTaskScheduler(request.getIntervalTaskName()));
            intervalTaskGroup.setIntervalTasks(intervalTasks);
            return new ResponseEntity<>(
                    intervalTaskGroupService.saveIntervalTaskGroup(intervalTaskGroup),
                    HttpStatus.CREATED
            );
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("The interval task group does not exist. Please try again")
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
            value="/delete-applied-quarterly/{taskId}", method=RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteIntervalTaskGroupAppliedQuarterly(
            @PathVariable(name = "taskId") Long taskId
    ) {
        if (intervalTaskGroupService
                .getIntervalTaskGroupAppliedQuarterly(taskId) == null
        ) {
            return new ResponseEntity<>(
                    new ApiError("Deletion Failed. Item not found"),
                    HttpStatus.NOT_FOUND
            );
        }
        intervalTaskGroupService
                .deleteIntervalTaskGroupAppliedQuarterly(taskId);
        return new ResponseEntity<>(
                new TaskDeletionResponse(
                        taskId, "Interval task group application successfully deleted!"
                )
                , HttpStatus.OK
        );
    }


    @RequestMapping(
            value="/delete-scheduler/{intervalTaskId}/{taskGroupId}", method=RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteIntervalTaskScheduler(
            @PathVariable(name = "taskGroupId") Long taskGroupId,
            @PathVariable(name = "intervalTaskId") Long intervalTaskId

    ) {
        try {
            IntervalTaskGroup intervalTaskGroup =
                    intervalTaskGroupService.getIntervalTaskGroup(
                            taskGroupId
                    );
            IntervalTaskScheduler intervalTaskScheduler = intervalTaskGroupService
                        .getIntervalTask(
                            intervalTaskId
                    );
            List<IntervalTaskScheduler> intervalTasks = intervalTaskGroup.getIntervalTasks();
            // the approach of removing by index, will cause an exception to be thrown
            // if the id is for a non-existent scheduler so that an error message will be
            // returned
            intervalTasks.remove(intervalTasks.indexOf(intervalTaskScheduler));
            intervalTaskGroup.setIntervalTasks(intervalTasks);
            intervalTaskGroupService.deleteIntervalTask(intervalTaskId);
            return new ResponseEntity<>(
                    intervalTaskGroupService.saveIntervalTaskGroup(intervalTaskGroup),
                    HttpStatus.OK
            );
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("The interval task group or task does not exist. Please try again")
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
            value="/delete-group/{taskGroupId}", method=RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteIntervalTaskGroup(
            @PathVariable(name = "taskGroupId") Long taskGroupId
    ) {
        if (intervalTaskGroupService
                .getIntervalTaskGroup(taskGroupId) == null
        ) {
            return new ResponseEntity<>(
                    new ApiError("Deletion Failed. Item not found") ,
                    HttpStatus.NOT_FOUND
            );
        }
        intervalTaskGroupService.deleteIntervalTaskGroup(taskGroupId);
        return new ResponseEntity<>(
                new TaskDeletionResponse(
                        taskGroupId,
                        "Interval task group successfully deleted!"
                )
                , HttpStatus.OK);
    }

    @GetMapping("/groups")
    public ResponseEntity<?> getUsersIntervalTaskGroups(
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            List<IntervalTaskGroup> responseData = intervalTaskGroupService
                    .getAllUsersIntervalTaskGroups(user.getUsername());
            for (IntervalTaskGroup entity : responseData) {
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
