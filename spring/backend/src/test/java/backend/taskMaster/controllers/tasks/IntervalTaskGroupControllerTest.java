package backend.taskMaster.controllers.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.*;
import backend.taskMaster.models.tasks.appliedSchedulers.IntervalTaskGroupAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskScheduler;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.user.UserRepository;
import backend.taskMaster.services.auth.JwtService;
import backend.taskMaster.services.tasks.IntervalTaskGroupService;
import backend.taskMaster.services.tasks.TaskService;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import backend.taskMaster.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(IntervalTaskGroupController.class)
@ContextConfiguration(classes = {TaskMasterApplication.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class IntervalTaskGroupControllerTest {

    @MockBean
    JwtService jwtService;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    IntervalTaskGroupService intervalTaskGroupService;

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


    IntervalTaskScheduler testIntervalTask1 = IntervalTaskScheduler.builder()
            .id(1L)
            .intervalTaskName("Test Interval Task 1")
            .build();

    IntervalTaskSchedulerPostRequest
            intervalTaskSchedulerPostRequest = IntervalTaskSchedulerPostRequest
            .builder()
            .intervalTaskGroupId(1L)
            .intervalTaskName("Test Interval Task 1")
            .build();

    IntervalTaskScheduler testIntervalTask2 = IntervalTaskScheduler.builder()
            .id(2L)
            .intervalTaskName("Test Interval Task 2")
            .build();


    IntervalTaskGroup testIntervalTaskGroup1 = IntervalTaskGroup.builder()
            .id(1L)
            .intervalTasks((List<IntervalTaskScheduler>) new ArrayList<IntervalTaskScheduler>())
            .taskGroupName("Test Interval Task Group 1")
            .intervalInDays(3)
            .taskGroupOwner(testUser)
            .build();

    IntervalTaskGroupPostRequest
            testIntervalTaskGroupRequest = IntervalTaskGroupPostRequest
            .builder()
            .taskGroupName("Test Interval Task Group 1")
            .intervalInDays(3)
            .build();

    TaskDeletionResponse
            intervalTaskGroupDeletionResponse = TaskDeletionResponse
            .builder()
            .id(testIntervalTaskGroup1.getId())
            .message("Interval task group successfully deleted!")
            .build();

    IntervalTaskGroup testIntervalTaskGroup2 = IntervalTaskGroup.builder()
            .id(2L)
            .taskGroupName("Test Interval Task Group 2")
            .intervalInDays(3)
            .taskGroupOwner(testUser)
            .build();

    IntervalTaskGroupAppliedQuarterly testITGAQ1 = IntervalTaskGroupAppliedQuarterly
            .builder()
            .id(1L)
            .intervalTaskGroup(testIntervalTaskGroup1)
            .year(2024)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    RecurringTaskAppliedQuarterlyPostRequest
            recurringTaskAppliedQuarterlyPostRequest = RecurringTaskAppliedQuarterlyPostRequest
            .builder()
            .year(LocalDate.now().getYear())
            .quarter(quarter)
            .recurringTaskSchedulerId(testIntervalTaskGroup1.getId())
            .build();

    IntervalTaskGroupAppliedQuarterly testITGAQ2 = IntervalTaskGroupAppliedQuarterly
            .builder()
            .id(2L)
            .intervalTaskGroup(testIntervalTaskGroup2)
            .year(2024)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();


    @Test
    void getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear() {
    }

    @Test
    void saveNewQuarterlyIntervalTaskGroup() {
    }

    @Test
    void createIntervalTaskGroup() {
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void createIntervalTaskScheduler() throws Exception {
        when(intervalTaskGroupService.getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup1);
        testIntervalTaskGroup1.generateTemplateSelectorString();
        when(intervalTaskGroupService
                .saveIntervalTaskGroup(any(IntervalTaskGroup.class)))
                .thenReturn(testIntervalTaskGroup1);
        mockMvc.perform(post("/api/interval/create-scheduler")
                .contentType("application/json")
                .with(csrf())
                .content(TestUtil.convertObjectToJsonBytes(
                        intervalTaskSchedulerPostRequest)
                )
        )
        //.andDo(print())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("taskGroupName")
                        .value(
                                testIntervalTaskGroup1.getTaskGroupName())
                )
                .andExpect(jsonPath("templateSelectorString").value(
                        "Test Interval Task Group 1 (Every 3 days)")
                )
                .andExpect(jsonPath("intervalInDays")
                        .value(testIntervalTaskGroup1.getIntervalInDays())
                )
                .andExpect(jsonPath("intervalTasks", hasSize(1)))
                .andExpect(jsonPath("intervalTasks[0].intervalTaskName")
                        .value(testIntervalTask1.getIntervalTaskName())
                );

    }

    @Test
    void deleteIntervalTaskGroupAppliedQuarterly() {
    }

    @Test
    void deleteIntervalTaskGroup() {
    }

    @Test
    void deleteIntervalTaskGroupScheduler() {
    }

    @Test
    void getUsersIntervalTaskGroups() {
    }
}