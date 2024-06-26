package backend.taskMaster.controllers.tasks;

import backend.taskMaster.models.errors.ApiError;
import backend.taskMaster.models.tasks.apiData.TaskDeletionResponse;
import backend.taskMaster.models.tasks.apiData.TaskPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskReschedulePatchRequest;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.task.TaskStatusEnum;
import backend.taskMaster.models.user.User;
import backend.taskMaster.services.tasks.TaskService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class BasicTaskController {
    @Autowired
    TaskService taskService;

    @Autowired
    UserDetailsServiceImplementation userService;

    @RequestMapping("/confirm/{id}")
    public ResponseEntity<?> confirmTaskCompletion(
            @PathVariable(name = "id") Long taskId
    ) {
        System.out.println("************calling method in controller****************");

        try {
            SingleTask task = taskService.getTask(taskId);
            return new ResponseEntity<>(
                    taskService.confirmTaskCompletion(task),
                    HttpStatus.OK);

        } catch (NullPointerException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @RequestBody TaskPostRequest request,
            Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            User user = userService.loadUserByUsername(userDetails.getUsername());
            LocalDate date = LocalDate.parse(request.getDate());
            SingleTask singleTask = new SingleTask(
                    request.getTaskName(),
                    date, user);
            return new ResponseEntity<>(
                    taskService
                            .saveTaskAndReturnAllOnSameDate(singleTask),
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

    @GetMapping("/current-month")
    public ResponseEntity<?> getUserTasksForCurrentMonth(
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        LocalDate today = LocalDate.now();
        LocalDate monthBegin = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        try {
            return new ResponseEntity<>(taskService
                    .getAllUserTasksInDateRange(
                            user.getUsername(), monthBegin, monthEnd), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }


    @GetMapping("date/{date}")
    public ResponseEntity<?> getUserTasksByDate (
            @PathVariable(name = "date") String date,
            Authentication authentication) {
        System.out.println("*********" + date + "***************");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        LocalDate queryDate = LocalDate.parse(date);
        try {
            return new ResponseEntity<>(taskService
                    .getAllUserTasksByDate(
                            userDetails.getUsername(), queryDate
                    )
                    , HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    @RequestMapping(value="/delete/{taskId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteTask(@PathVariable(name = "taskId") Long taskId) {
        if (taskService.getTask(taskId) == null) {
            return new ResponseEntity<>(
                    new ApiError("Deletion Failed. Item not found") , HttpStatus.NOT_FOUND
            );
        }
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(
                new TaskDeletionResponse(
                        taskId, "Task successfully deleted!"
                )
                , HttpStatus.OK);
    }

    @GetMapping("/month-year/{month}/{year}")
    public ResponseEntity<?> getUserTasksByMonthAndYear(
            @PathVariable(name = "month") Integer month,
            @PathVariable(name = "year") Integer year,
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        LocalDate monthBegin = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthBegin.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        try {
            return new ResponseEntity<>(taskService
                    .getAllUserTasksInDateRange(
                            user.getUsername(), monthBegin, monthEnd), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    @PatchMapping("/reschedule/{taskId}")
    public ResponseEntity<?> rescheduleTask(
            @PathVariable(name = "taskId") Long taskId,
            @RequestBody TaskReschedulePatchRequest taskReschedulePatchRequest
            ) {
        try {
            SingleTask updatedTask = taskService.getTask(taskId);
            LocalDate newDate = LocalDate.parse(taskReschedulePatchRequest.getDate());
            updatedTask.setDate(newDate);
            updatedTask.setStatus(TaskStatusEnum.DEFERRED);
            updatedTask.setComments(taskReschedulePatchRequest.getComments());
            updatedTask.setUpdatedDateTime(LocalDateTime.now());
            return new ResponseEntity<>(
                    taskService.saveTask(updatedTask),
                    HttpStatus.OK);
        } catch (NullPointerException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    // todo: write endpoint and controller tests
    @GetMapping("/unconfirmed")
    public ResponseEntity<?> getUserTasksByMonthAndYear(
            Authentication authentication
    ) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        try {
            return new ResponseEntity<>(taskService
                    .getAllUncompletedPastUserTasks(
                            user.getUsername()), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }
}
