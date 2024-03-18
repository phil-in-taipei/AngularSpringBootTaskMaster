package backend.taskMaster.services.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.task.TaskStatusEnum;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.tasks.task.SingleTaskRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes= TaskMasterApplication.class)
@ActiveProfiles("test")
class TaskServiceTest {

    @MockBean
    SingleTaskRepo singleTaskRepo;

    @Autowired
    TaskService taskService;

    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();
    SingleTask testTask1 = SingleTask.builder()
            .id(1L)
            .taskName("Test Task 1")
            .date(LocalDate.now().minusDays(1))
            .user(testUser)
            .build();
    SingleTask testTask2 = SingleTask.builder()
            .id(1L)
            .taskName("Test Task 2")
            .date(LocalDate.now().minusDays(2))
            .user(testUser)
            .build();

    @Test
    void confirmTaskCompletion() {
        when(singleTaskRepo.save(any(SingleTask.class))).thenReturn(testTask1);
        SingleTask completedTask = taskService.confirmTaskCompletion(testTask1);
        assertThat(completedTask.getStatus()).isEqualTo(TaskStatusEnum.COMPLETED);
        verify(singleTaskRepo, times(1)).save(testTask1);
    }

    @Test
    void deleteTask() {
        Long taskId = 1L;
        taskService.deleteTask(taskId);
        verify(singleTaskRepo, times(1)).deleteById(taskId);
    }

    @Test
    void getAllUserTasksInDateRange() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfThisMonth = firstDayOfThisMonth.plusMonths(1)
                .minusDays(1);
        testTask1.setDate(firstDayOfThisMonth.plusDays(2));
        testTask2.setDate(firstDayOfThisMonth.plusDays(3));
        List<SingleTask> tasksInCurrentMonth = new ArrayList<>();
        tasksInCurrentMonth.add(testTask1);
        tasksInCurrentMonth.add(testTask2);
        when(singleTaskRepo.findAllByUserUsernameAndDateBetweenOrderByDateAsc(
                        anyString(),  eq(firstDayOfThisMonth), eq(lastDayOfThisMonth)
                )
        ).thenReturn(tasksInCurrentMonth);
        assertThat(
                taskService
                        .getAllUserTasksInDateRange("testuser", firstDayOfThisMonth,
                                lastDayOfThisMonth))
                .isEqualTo(tasksInCurrentMonth);
        assertThat(
                taskService
                        .getAllUserTasksInDateRange("testuser", firstDayOfThisMonth,
                                lastDayOfThisMonth).size())
                .isEqualTo(tasksInCurrentMonth.size());
    }

    @Test
    void getAllUserTasksByDate() {
        List<SingleTask> tasksOnCurrentDate = new ArrayList<>();
        tasksOnCurrentDate.add(testTask1);
        tasksOnCurrentDate.add(testTask2);
        when(singleTaskRepo.findAllByUserUsernameAndDate(anyString(),  eq(LocalDate.now()))
        ).thenReturn(tasksOnCurrentDate);
        assertThat(
                taskService.getAllUserTasksByDate("testuser", LocalDate.now()))
                .isEqualTo(tasksOnCurrentDate);
        assertThat(
                taskService.getAllUserTasksByDate("testuser", LocalDate.now()).size())
                .isEqualTo(tasksOnCurrentDate.size());
    }

    @Test
    void getAllUncompletedPastUserTasks() {
        List<SingleTask> tasksOnPreviousDates = new ArrayList<>();
        tasksOnPreviousDates.add(testTask1);
        tasksOnPreviousDates.add(testTask2);
        when(singleTaskRepo
                        .findByStatusIsNotAndDateBeforeAndUserUsername(
                                eq(TaskStatusEnum.COMPLETED),
                                eq(LocalDate.now()), anyString())
        ).thenReturn(tasksOnPreviousDates);
        assertThat(
                taskService.getAllUncompletedPastUserTasks(
                        "testuser"))
                .isEqualTo(tasksOnPreviousDates);
        assertThat(
                taskService.getAllUncompletedPastUserTasks("testuser")
                        .size())
                .isEqualTo(tasksOnPreviousDates.size());
    }

    @Test
    void getTask() {
        when(singleTaskRepo.findById(anyLong()))
                .thenReturn(Optional.of(testTask1));
        assertThat(taskService.getTask(1L))
                .isEqualTo(testTask1);
    }

    @Test
    void getTaskFailure() {
        // this does not return an error (returns null if no task exists with the id)
        when(singleTaskRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(taskService.getTask(2L))
                .isEqualTo(null);
    }

    @Test
    void saveBatchOfTasks() {
        List<SingleTask> tasks = new ArrayList<>();
        tasks.add(testTask1);
        tasks.add(testTask2);
        when(singleTaskRepo.saveAll(anyList())).thenReturn(tasks);
        taskService.saveBatchOfTasks(tasks);
        verify(singleTaskRepo, times(1)).saveAll(tasks);
    }

    @Test
    void saveBatchOfTasksFailure() {
        List<SingleTask> tasks = new ArrayList<>();
        tasks.add(testTask1);
        tasks.add(testTask2);
        when(singleTaskRepo.saveAll(anyList()))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.saveBatchOfTasks(tasks);
        });
    }

    @Test
    void saveTask() throws IllegalArgumentException {
        when(singleTaskRepo.save(any(SingleTask.class)))
                .thenReturn(testTask1);
        assertThat(taskService.saveTask(testTask1))
                .isEqualTo(testTask1);
    }

    @Test
    void saveTaskFailure() throws IllegalArgumentException {
        when(singleTaskRepo.save(any(SingleTask.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.saveTask(testTask1);
        });
    }
}