package backend.taskMaster.controllers.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.apiData.RecurringTaskAppliedQuarterlyPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskDeletionResponse;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.user.UserRepository;
import backend.taskMaster.services.auth.JwtService;
import backend.taskMaster.services.tasks.MonthlyTaskSchedulingService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MonthlyTaskSchedulerController.class)
@ContextConfiguration(classes = {TaskMasterApplication.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class MonthlyTaskSchedulerControllerTest {

    @MockBean
    JwtService jwtService;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TaskService taskService;

    @MockBean
    UserDetailsServiceImplementation userService;

    String quarter = "Q2";

    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();


    MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
            .id(1L)
            .monthlyTaskName("Test Monthly Task Scheduler 1")
            .dayOfMonth(1)
            .user(testUser)
            .build();

    TaskDeletionResponse
            monthlyTaskSchedulerDeletionResponse = TaskDeletionResponse
            .builder()
            .id(testMonthlyTaskScheduler.getId())
            .message("Monthly task scheduler successfully deleted!")
            .build();

    MonthlySchedulerPostRequest
            testMonthlyTaskSchedulerRequest = MonthlySchedulerPostRequest
            .builder()
            .monthlyTaskName("Test Monthly Task Scheduler 1")
            .dayOfMonth(1)
            .build();

    MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
            MonthlyTaskAppliedQuarterly
                    .builder()
                    .id(1L)
                    .monthlyTaskScheduler(testMonthlyTaskScheduler)
                    .year(2024)
                    .quarter(QuarterlySchedulingEnum.Q2)
                    .build();

    TaskDeletionResponse
            testMonthlyTaskAppliedQuarterlyDeletionResponse = TaskDeletionResponse
            .builder()
            .id(testMonthlyTaskAppliedQuarterly.getId())
            .message("Monthly task application successfully deleted!")
            .build();

    RecurringTaskAppliedQuarterlyPostRequest
            recurringTaskAppliedQuarterlyPostRequest = RecurringTaskAppliedQuarterlyPostRequest
            .builder()
            .year( LocalDate.now().getYear())
            .quarter(quarter)
            .recurringTaskSchedulerId(testMonthlyTaskScheduler.getId())
            .build();

    MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
            .id(2L)
            .monthlyTaskName("Test Monthly Task Scheduler 2")
            .dayOfMonth(2)
            .user(testUser)
            .build();

    MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly2 =
            MonthlyTaskAppliedQuarterly
                    .builder()
                    .id(2L)
                    .monthlyTaskScheduler(testMonthlyTaskScheduler2)
                    .year(2024)
                    .quarter(QuarterlySchedulingEnum.Q2)
                    .build();

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void createMonthlyTaskScheduler() throws Exception {
        int dayOfMonth = 1;
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(any(MonthlyTaskScheduler.class)))
                .thenReturn(testMonthlyTaskScheduler);
        mockMvc.perform(post("/api/monthly/create")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                testMonthlyTaskSchedulerRequest)
                        )
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("monthlyTaskName")
                        .value(
                                testMonthlyTaskSchedulerRequest.getMonthlyTaskName())
                )
                .andExpect(jsonPath("templateSelectorString").value(
                        "Test Monthly Task Scheduler 1: 1st day of month ")
                )
                .andExpect(jsonPath("dayOfMonth")
                        .value(dayOfMonth)
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUsersMonthlyTaskSchedulers() throws Exception {
        List<MonthlyTaskScheduler> usersMonthlyTaskSchedulers = new ArrayList<>();
        testMonthlyTaskScheduler.generateTemplateSelectorString();
        testMonthlyTaskScheduler2.generateTemplateSelectorString();
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler);
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulers(anyString()))
                .thenReturn(usersMonthlyTaskSchedulers);
        mockMvc.perform(get("/api/monthly/schedulers")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]monthlyTaskName")
                        .value(
                                testMonthlyTaskSchedulerRequest.getMonthlyTaskName())
                )
                .andExpect(jsonPath("$[0]templateSelectorString").value(
                        "Test Monthly Task Scheduler 1: 1st day of month ")
                )
                .andExpect(jsonPath("$[0]dayOfMonth")
                        .value(testMonthlyTaskSchedulerRequest.getDayOfMonth())
                )
                .andExpect(jsonPath("$[1]monthlyTaskName")
                        .value(
                                testMonthlyTaskScheduler2.getMonthlyTaskName())
                )
                .andExpect(jsonPath("$[1]templateSelectorString").value(
                        "Test Monthly Task Scheduler 2: 2nd day of month ")
                )
                .andExpect(jsonPath("$[1]dayOfMonth")
                        .value(testMonthlyTaskScheduler2.getDayOfMonth())
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void testSaveNewQuarterlyMonthlyTask() throws Exception  {
        int thisYear = LocalDate.now().getYear();
        when(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        any(MonthlyTaskAppliedQuarterly.class))
        ).thenReturn(testMonthlyTaskAppliedQuarterly);
        mockMvc.perform(post("/api/monthly/apply-quarterly")
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
                .andExpect(jsonPath("monthlyTaskSchedulerId")
                        .value(
                                testMonthlyTaskScheduler.getId())
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
    void getUsersMonthlyTaskSchedulersAppliedQuarterlyByQuarterAndYear()
            throws Exception
    {
        List<MonthlyTaskAppliedQuarterly> usersMTAQ = new ArrayList<>();
        usersMTAQ.add(testMonthlyTaskAppliedQuarterly);
        usersMTAQ.add(testMonthlyTaskAppliedQuarterly2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                        eq(QuarterlySchedulingEnum.Q2),
                        anyInt(), anyString()))
                .thenReturn(usersMTAQ);
        int thisYear = LocalDate.now().getYear();
        mockMvc.perform(get("/api/monthly/applied-quarterly/"
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
                .andExpect(jsonPath("$[0]monthlyTaskSchedulerId")
                        .value(
                                testMonthlyTaskScheduler.getId())
                )
                .andExpect(jsonPath("$[0]quarter").value(
                        quarter)
                )
                .andExpect(jsonPath("$[0]year")
                        .value(thisYear
                        ))
                .andExpect(jsonPath("$[1]monthlyTaskSchedulerId")
                        .value(
                                testMonthlyTaskScheduler2.getId())
                );

    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteMonthlyTaskScheduler() throws Exception {
        when(monthlyTaskSchedulingService.getMonthlyTaskScheduler(anyLong()))
                .thenReturn(testMonthlyTaskScheduler);
        mockMvc.
                perform(request(
                        HttpMethod.DELETE, "/api/monthly/delete/" +
                                testMonthlyTaskScheduler.getId()
                        )
                        .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                monthlyTaskSchedulerDeletionResponse.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                monthlyTaskSchedulerDeletionResponse.getMessage()
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteMonthlyTaskSchedulerFailure() throws Exception {
        mockMvc.
                perform(request(
                                HttpMethod.DELETE, "/api/monthly/delete/" +
                                        testMonthlyTaskScheduler.getId()
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
    public void deleteMonthlyTaskAppliedQuarterly() throws Exception {
        when(monthlyTaskSchedulingService.getMonthlyTaskAppliedQuarterly(anyLong()))
                .thenReturn(testMonthlyTaskAppliedQuarterly);
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/monthly/delete-applied-quarterly/" +
                                testMonthlyTaskAppliedQuarterly.getId())
                        .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                testMonthlyTaskAppliedQuarterly.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                "Monthly task application successfully deleted!"
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteMonthlyTaskAppliedQuarterlyFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/monthly/delete-applied-quarterly/" +
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

}
