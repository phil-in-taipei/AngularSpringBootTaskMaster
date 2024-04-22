package backend.taskMaster.controllers.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.TaskPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskReschedulePatchRequest;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.task.TaskStatusEnum;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.user.UserRepository;
import backend.taskMaster.services.auth.AuthenticationService;
import backend.taskMaster.services.auth.JwtService;
import backend.taskMaster.services.tasks.TaskService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import backend.taskMaster.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@WebMvcTest(BasicTaskController.class)
@ContextConfiguration(classes = {TaskMasterApplication.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class BasicTaskControllerTest {

    @MockBean
    JwtService jwtService;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TaskService taskService;

    @MockBean
    UserDetailsServiceImplementation userService;

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
            .id(2L)
            .taskName("Test Task 2")
            .date(LocalDate.now().minusDays(2))
            .user(testUser)
            .build();

    TaskPostRequest testCreateRequest = TaskPostRequest.builder()
            .taskName("Test Task 1")
            .date(LocalDate.now().minusDays(1).toString())
            .build();

    TaskReschedulePatchRequest reschedulePatchRequest = TaskReschedulePatchRequest
            .builder()
            .date(LocalDate.now().toString())
            .comments("Rescheduling")
            .build();

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void createTask() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<SingleTask> tasksYesterday = new ArrayList<>();
        tasksYesterday.add(testTask1);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(taskService.saveTaskAndReturnAllOnSameDate(any(SingleTask.class)))
                .thenReturn(tasksYesterday);
        mockMvc.perform(post("/api/task/create")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(testCreateRequest))
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]taskName")
                        .value(
                                "Test Task 1")
                )
                .andExpect(jsonPath("$[0]date")
                        .value(
                                yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUserTasksForCurrentMonth() throws Exception {
        LocalDate today = LocalDate.now();
        int thisMonthInt = today.getMonthValue();
        int thisYearInt = today.getYear();
        LocalDate monthBegin = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1).
                withDayOfMonth(1).minusDays(1);
        testTask1.setDate(LocalDate.of(thisYearInt, 1, thisMonthInt));
        testTask2.setDate(LocalDate.of(thisYearInt, 5, thisMonthInt));
        List<SingleTask> tasksInCurrentMonth = new ArrayList<>();
        tasksInCurrentMonth.add(testTask1);
        tasksInCurrentMonth.add(testTask2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(taskService.getAllUserTasksInDateRange(anyString(),
                eq(monthBegin), eq(monthEnd)))
                .thenReturn(tasksInCurrentMonth);
        mockMvc.perform(get("/api/task/current-month")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taskName").value("Test Task 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].taskName").value("Test Task 2"));
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUserTasksByDate() throws Exception {
        String today = LocalDate.now().toString();
        List<SingleTask> tasksOnCurrentDate = new ArrayList<>();
        tasksOnCurrentDate.add(testTask1);
        tasksOnCurrentDate.add(testTask2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(taskService.getAllUserTasksByDate(anyString(), eq(LocalDate.now())))
                .thenReturn(tasksOnCurrentDate);
        mockMvc.perform(get("/api/task/date/" + today))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taskName").value("Test Task 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].taskName").value("Test Task 2"));
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteTask() throws Exception {
        when(taskService.getTask(anyLong()))
                .thenReturn(testTask1);
        mockMvc.
                perform(
                        request(HttpMethod.DELETE, "/api/task/delete/" + testTask1.getId())
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                1
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                "Task successfully deleted!"
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteTaskFailure() throws Exception {
        long nonExistentID = 12920L;
        when(taskService.getTask(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(
                        request(HttpMethod.DELETE,
                                "/api/task/delete/" + nonExistentID)
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(
                                "Deletion Failed. Item not found"
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUserTasksByMonthAndYear() throws Exception{
        LocalDate today = LocalDate.now();
        int thisMonthInt = today.getMonthValue();
        int thisYearInt = today.getYear();
        LocalDate monthBegin = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1).
                withDayOfMonth(1).minusDays(1);
        testTask1.setDate(LocalDate.of(thisYearInt, 1, thisMonthInt));
        testTask2.setDate(LocalDate.of(thisYearInt, 5, thisMonthInt));
        List<SingleTask> tasksInCurrentMonth = new ArrayList<>();
        tasksInCurrentMonth.add(testTask1);
        tasksInCurrentMonth.add(testTask2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(taskService.getAllUserTasksInDateRange(anyString(),
                eq(monthBegin), eq(monthEnd)))
                .thenReturn(tasksInCurrentMonth);
        mockMvc
                .perform(get("/api/task/month-year/"
                        + String.valueOf(thisMonthInt) + "/"
                        + String.valueOf(thisYearInt)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].taskName").value("Test Task 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].taskName").value("Test Task 2"));
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void rescheduleTask() throws Exception {
        LocalDate today = LocalDate.now();
        when(taskService.getTask(anyLong()))
                .thenReturn(testTask1);
        testTask1.setDate(LocalDate.now());
        testTask1.setComments("Test rescheduling of task");
        testTask1.setStatus(TaskStatusEnum.DEFERRED);
        testTask1.setUpdatedDateTime(LocalDateTime.now());
        when(taskService.saveTask(any(SingleTask.class)))
                .thenReturn(testTask1);
        mockMvc.perform(patch("/api/task/reschedule/1")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(reschedulePatchRequest))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("taskName")
                        .value(
                                "Test Task 1")
                )
                .andExpect(jsonPath("date")
                        .value(
                                today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void rescheduleTaskFailure() throws Exception {
        when(taskService.getTask(anyLong()))
                .thenReturn(null);
        mockMvc.perform(patch("/api/task/reschedule/1")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(reschedulePatchRequest))
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "There was an error. Please try again")
                );
    }
}