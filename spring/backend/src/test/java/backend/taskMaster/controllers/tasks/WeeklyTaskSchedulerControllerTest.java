package backend.taskMaster.controllers.tasks;

import backend.taskMaster.TaskMasterApplication;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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


    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    WeeklyTaskScheduler weeklyTaskScheduler = WeeklyTaskScheduler.builder()
            .id(1L)
            .weeklyTaskName("Test Weekly Task Scheduler")
            .dayOfWeek(DayOfWeek.MONDAY)
            .user(testUser)
            .build();

    WeeklySchedulerPostRequest weeklySchedulerPostRequest = WeeklySchedulerPostRequest
            .builder()
            .weeklyTaskName("Test Weekly Task Scheduler")
            .dayOfWeek("MONDAY")
            .build();

    WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly
            = WeeklyTaskAppliedQuarterly
            .builder()
            .weeklyTaskScheduler(weeklyTaskScheduler)
            .year(2023)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    WeeklyTaskScheduler weeklyTaskScheduler2 = WeeklyTaskScheduler.builder()
            .id(1L)
            .weeklyTaskName("Test Weekly Task Scheduler 2")
            .dayOfWeek(DayOfWeek.TUESDAY)
            .user(testUser)
            .build();

    WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly2
            = WeeklyTaskAppliedQuarterly
            .builder()
            .weeklyTaskScheduler(weeklyTaskScheduler2)
            .year(2023)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    @Test
    void getUsersWeeklyTaskSchedulersAppliedQuarterly() {
    }

    @Test
    void getUsersWeeklyTaskSchedulersAppliedQuarterlyByQuarterAndYear() {
    }

    @Test
    void saveNewQuarterlyWeeklyTask() {
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void createWeeklyTaskScheduler() throws Exception{
        String dayOfWeek= "MONDAY";
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(weeklyTaskSchedulingService
                .saveWeeklyTaskScheduler(any(WeeklyTaskScheduler.class)))
                .thenReturn(weeklyTaskScheduler);
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
    void deleteWeeklyTaskScheduler() {
    }

    @Test
    void deleteWeeklyTaskAppliedQuarterly() {
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUsersWeeklyTaskSchedulers() throws Exception {
        List<WeeklyTaskScheduler> usersWeeklyTaskSchedulers = new ArrayList<>();
        weeklyTaskScheduler.generateTemplateSelectorString();
        weeklyTaskScheduler2.generateTemplateSelectorString();
        usersWeeklyTaskSchedulers.add(weeklyTaskScheduler);
        usersWeeklyTaskSchedulers.add(weeklyTaskScheduler2);
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
                                weeklyTaskScheduler.getWeeklyTaskName())
                )
                .andExpect(jsonPath("$[0]templateSelectorString").value(
                        weeklyTaskScheduler.getWeeklyTaskName() + " (every " + "Monday" + ")"
                        )
                )
                .andExpect(jsonPath("$[0]dayOfWeek")
                        .value(weeklyTaskScheduler.getDayOfWeek().toString())
                )
                .andExpect(jsonPath("$[1]weeklyTaskName")
                        .value(
                                weeklyTaskScheduler2.getWeeklyTaskName())
                )
                .andExpect(jsonPath("$[1]templateSelectorString").value(
                        weeklyTaskScheduler2.getWeeklyTaskName() + " (every " + "Tuesday" + ")"                        )
                )
                .andExpect(jsonPath("$[1]dayOfWeek")
                        .value(weeklyTaskScheduler2.getDayOfWeek().toString())
                );
    }
}