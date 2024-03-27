package backend.taskMaster.controllers.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.user.UserRepository;
import backend.taskMaster.services.auth.AuthenticationService;
import backend.taskMaster.services.auth.JwtService;
import backend.taskMaster.services.tasks.MonthlyTaskSchedulingService;
import backend.taskMaster.services.tasks.TaskService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import backend.taskMaster.services.utils.GenerateDatesService;
import backend.taskMaster.services.utils.GenerateTaskBatchesService;
import backend.taskMaster.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    MonthlySchedulerPostRequest testMonthlyTaskSchedulerRequest = MonthlySchedulerPostRequest
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
                        .value(1)
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
                .andDo(print())
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
}