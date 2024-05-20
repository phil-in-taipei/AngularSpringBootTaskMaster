package backend.taskMaster.controllers.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.RecurringTaskAppliedQuarterlyPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskDeletionResponse;
import backend.taskMaster.models.tasks.apiData.WeeklySchedulerPostRequest;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.taskSchedulers.WeeklyTaskScheduler;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.user.UserRepository;
import backend.taskMaster.services.auth.JwtService;
import backend.taskMaster.services.tasks.TaskService;
import backend.taskMaster.services.tasks.WeeklyTaskSchedulingService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import backend.taskMaster.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeeklyTaskSchedulerController.class)
@ContextConfiguration(classes = {TaskMasterApplication.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class WeeklyTaskSchedulerControllerTest {

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

    @MockBean
    WeeklyTaskSchedulingService weeklyTaskSchedulingService;

    String quarter = "Q2";

    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    WeeklyTaskScheduler testWeeklyTaskScheduler = WeeklyTaskScheduler.builder()
            .id(1L)
            .weeklyTaskName("Test Weekly Task Scheduler")
            .dayOfWeek(DayOfWeek.MONDAY)
            .user(testUser)
            .build();

    TaskDeletionResponse
            weeklyTaskSchedulerDeletionResponse = TaskDeletionResponse
            .builder()
            .id(testWeeklyTaskScheduler.getId())
            .message("Weekly task scheduler successfully deleted!")
            .build();

    RecurringTaskAppliedQuarterlyPostRequest
            recurringTaskAppliedQuarterlyPostRequest = RecurringTaskAppliedQuarterlyPostRequest
            .builder()
            .year( LocalDate.now().getYear())
            .quarter(quarter)
            .recurringTaskSchedulerId(testWeeklyTaskScheduler.getId())
            .build();

    WeeklySchedulerPostRequest weeklySchedulerPostRequest = WeeklySchedulerPostRequest
            .builder()
            .weeklyTaskName("Test Weekly Task Scheduler")
            .dayOfWeek("MONDAY")
            .build();

    WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly
            = WeeklyTaskAppliedQuarterly
            .builder()
            .id(1L)
            .weeklyTaskScheduler(testWeeklyTaskScheduler)
            .year(2024)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    WeeklyTaskScheduler
            testWeeklyTaskScheduler2 = WeeklyTaskScheduler.builder()
            .id(2L)
            .weeklyTaskName("Test Weekly Task Scheduler 2")
            .dayOfWeek(DayOfWeek.TUESDAY)
            .user(testUser)
            .build();

    WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly2
            = WeeklyTaskAppliedQuarterly
            .builder()
            .id(2L)
            .weeklyTaskScheduler(testWeeklyTaskScheduler2)
            .year(2024)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    TaskDeletionResponse
            testWeeklyTaskAppliedQuarterlyDeletionResponse = TaskDeletionResponse
            .builder()
            .id(testWeeklyTaskAppliedQuarterly.getId())
            .message("Weekly task application successfully deleted!")
            .build();

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUsersWeeklyTaskSchedulersAppliedQuarterlyByQuarterAndYear()
            throws Exception
    {
        List<WeeklyTaskAppliedQuarterly> usersWTAQ = new ArrayList<>();
        usersWTAQ.add(testWeeklyTaskAppliedQuarterly);
        usersWTAQ.add(testWeeklyTaskAppliedQuarterly2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(weeklyTaskSchedulingService
                .getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                        eq(QuarterlySchedulingEnum.Q2),
                        anyInt(), anyString()))
                .thenReturn(usersWTAQ);
        int thisYear = LocalDate.now().getYear();
        mockMvc.perform(get("/api/weekly/applied-quarterly/"
                        + quarter + "/" + thisYear)
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header()
                        .string("Content-Type", "application/json")
                )
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]weeklyTaskSchedulerId")
                        .value(
                                testWeeklyTaskScheduler.getId())
                )
                .andExpect(jsonPath("$[0]quarter").value(
                        quarter)
                )
                .andExpect(jsonPath("$[0]year")
                        .value(thisYear
                        ))
                .andExpect(jsonPath("$[1]weeklyTaskSchedulerId")
                        .value(
                                testWeeklyTaskScheduler2.getId())
                );

    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void testSaveNewQuarterlyWeeklyTask() throws Exception  {
        int thisYear = LocalDate.now().getYear();
        when(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        any(WeeklyTaskAppliedQuarterly.class))
        ).thenReturn(testWeeklyTaskAppliedQuarterly);
        mockMvc.perform(post("/api/weekly/apply-quarterly")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                recurringTaskAppliedQuarterlyPostRequest)
                        )
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Content-Type", "application/json")
                )
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("weeklyTaskSchedulerId")
                        .value(
                                testWeeklyTaskScheduler.getId())
                )
                .andExpect(jsonPath("quarter").value(
                        quarter)
                )
                .andExpect(jsonPath("year")
                        .value(thisYear)
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void createWeeklyTaskScheduler() throws Exception{
        String dayOfWeek= "MONDAY";
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(weeklyTaskSchedulingService
                .saveWeeklyTaskScheduler(any(WeeklyTaskScheduler.class)))
                .thenReturn(testWeeklyTaskScheduler);
        mockMvc.perform(post("/api/weekly/create")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                weeklySchedulerPostRequest)
                        )
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("weeklyTaskName")
                        .value(
                                weeklySchedulerPostRequest.getWeeklyTaskName())
                )
                .andExpect(jsonPath("dayOfWeek")
                        .value(dayOfWeek)
                );
    }
    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteWeeklyTaskScheduler() throws Exception {
        when(weeklyTaskSchedulingService.getWeeklyTaskScheduler(anyLong()))
                .thenReturn(testWeeklyTaskScheduler);
        mockMvc.
                perform(request(
                                HttpMethod.DELETE, "/api/weekly/delete/" +
                                        testWeeklyTaskScheduler.getId()
                        )
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                weeklyTaskSchedulerDeletionResponse.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                weeklyTaskSchedulerDeletionResponse.getMessage()
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteWeeklyTaskSchedulerFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                                HttpMethod.DELETE, "/api/weekly/delete/" +
                                        nonExistentID
                        )
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
    public void deleteWeeklyTaskAppliedQuarterly() throws Exception {
        when(weeklyTaskSchedulingService.getWeeklyTaskAppliedQuarterly(anyLong()))
                .thenReturn(testWeeklyTaskAppliedQuarterly);
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/weekly/delete-applied-quarterly/" +
                                testWeeklyTaskAppliedQuarterly.getId())
                        .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                testWeeklyTaskAppliedQuarterly.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                testWeeklyTaskAppliedQuarterlyDeletionResponse.getMessage()
                        )
                );
    }


    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteWeeklyTaskAppliedQuarterlyFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/weekly/delete-applied-quarterly/" +
                                nonExistentID)
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
    void getUsersWeeklyTaskSchedulers() throws Exception {
        List<WeeklyTaskScheduler> usersWeeklyTaskSchedulers = new ArrayList<>();
        testWeeklyTaskScheduler.generateTemplateSelectorString();
        testWeeklyTaskScheduler2.generateTemplateSelectorString();
        usersWeeklyTaskSchedulers.add(testWeeklyTaskScheduler);
        usersWeeklyTaskSchedulers.add(testWeeklyTaskScheduler2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(weeklyTaskSchedulingService
                .getAllUsersWeeklyTaskSchedulers(anyString()))
                .thenReturn(usersWeeklyTaskSchedulers);
        mockMvc.perform(get("/api/weekly/schedulers")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]weeklyTaskName")
                        .value(
                                testWeeklyTaskScheduler.getWeeklyTaskName())
                )
                .andExpect(jsonPath("$[0]templateSelectorString").value(
                        testWeeklyTaskScheduler.getWeeklyTaskName()
                                + " (every " + "Monday" + ")"
                        )
                )
                .andExpect(jsonPath("$[0]dayOfWeek")
                        .value(testWeeklyTaskScheduler.getDayOfWeek().toString())
                )
                .andExpect(jsonPath("$[1]weeklyTaskName")
                        .value(
                                testWeeklyTaskScheduler2.getWeeklyTaskName())
                )
                .andExpect(jsonPath("$[1]templateSelectorString").value(
                        testWeeklyTaskScheduler2.getWeeklyTaskName()
                                + " (every " + "Tuesday" + ")"                        )
                )
                .andExpect(jsonPath("$[1]dayOfWeek")
                        .value(testWeeklyTaskScheduler2.getDayOfWeek().toString())
                );
    }
}