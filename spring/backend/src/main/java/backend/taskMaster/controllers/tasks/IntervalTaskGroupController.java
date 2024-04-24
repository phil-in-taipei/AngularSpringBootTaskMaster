package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.IntervalTaskGroupPostRequest;
import backend.taskMaster.models.tasks.apiData.IntervalTaskSchedulerPostRequest;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskDeletionResponse;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskScheduler;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
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
            return new ResponseEntity<>(
                    intervalTaskGroupService
                            .getAllUsersIntervalTaskGroups(user.getUsername()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

}
