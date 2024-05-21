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
import org.springframework.http.HttpMethod;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
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

    TaskDeletionResponse
            testIntervalTaskGroupAppliedQuarterlyDeletionResponse = TaskDeletionResponse
            .builder()
            .id(testITGAQ1.getId())
            .message("Interval task group application successfully deleted!")
            .build();

    IntervalTaskGroupAppliedQuarterly testITGAQ2 = IntervalTaskGroupAppliedQuarterly
            .builder()
            .id(2L)
            .intervalTaskGroup(testIntervalTaskGroup2)
            .year(2024)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();


    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear()
            throws Exception {
        List<IntervalTaskGroupAppliedQuarterly> usersITAQ = new ArrayList<>();
        usersITAQ.add(testITGAQ1);
        usersITAQ.add(testITGAQ2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(intervalTaskGroupService
                .getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
                        eq(QuarterlySchedulingEnum.Q2),
                        anyInt(), anyString()))
                .thenReturn(usersITAQ);
        int thisYear = LocalDate.now().getYear();
        mockMvc.perform(get("/api/interval/applied-quarterly/"
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
                .andExpect(jsonPath("$[0]intervalTaskGroupId")
                        .value(
                                testIntervalTaskGroup1.getId())
                )
                .andExpect(jsonPath("$[0]quarter").value(
                        quarter)
                )
                .andExpect(jsonPath("$[0]year")
                        .value(thisYear
                        ))
                .andExpect(jsonPath("$[1]intervalTaskGroupId")
                        .value(
                                testIntervalTaskGroup2.getId())
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void saveNewQuarterlyIntervalTaskGroup() throws Exception {
        int thisYear = LocalDate.now().getYear();
        when(intervalTaskGroupService
                .saveIntervalTaskGroupAppliedQuarterly(
                        any(IntervalTaskGroupAppliedQuarterly.class))
        ).thenReturn(testITGAQ1);
        mockMvc.perform(post("/api/interval/apply-quarterly")
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
                .andExpect(jsonPath("intervalTaskGroupId")
                        .value(
                                testITGAQ1.getId())
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
    void createIntervalTaskGroup() throws Exception {
        int dayOfMonth = 1;
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(intervalTaskGroupService
                .saveIntervalTaskGroup(any(IntervalTaskGroup.class)))
                .thenReturn(testIntervalTaskGroup1);
        mockMvc.perform(post("/api/interval/create-group")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                testIntervalTaskGroupRequest)
                        )
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("taskGroupName")
                        .value(
                                testIntervalTaskGroupRequest.getTaskGroupName())
                )
                .andExpect(jsonPath("templateSelectorString").value(
                        "Test Interval Task Group 1 (Every 3 days)")
                )
                .andExpect(jsonPath("intervalInDays")
                        .value(testIntervalTaskGroup1.getIntervalInDays())
                );
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
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void deleteIntervalTaskGroupAppliedQuarterly() throws Exception {
        when(intervalTaskGroupService.getIntervalTaskGroupAppliedQuarterly(anyLong()))
                .thenReturn(testITGAQ1);
        mockMvc.
                perform(request(
                                HttpMethod.DELETE, "/api/interval/delete-applied-quarterly/" +
                                        testITGAQ1.getId()
                        )
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                testITGAQ1.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                testIntervalTaskGroupAppliedQuarterlyDeletionResponse.getMessage()
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteIntervalTaskGroupAppliedQuarterlyFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                                HttpMethod.DELETE,
                        "/api/interval/delete-applied-quarterly/" +
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
    void deleteIntervalTaskGroup() throws Exception {
        when(intervalTaskGroupService.getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup1);
        mockMvc.
                perform(request(
                                HttpMethod.DELETE, "/api/interval/delete-group/" +
                                testIntervalTaskGroup1.getId()
                        )
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                intervalTaskGroupDeletionResponse.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                intervalTaskGroupDeletionResponse.getMessage()
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    public void deleteIntervalTaskGroupFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                                HttpMethod.DELETE, "/api/interval/delete-group/" +
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
    void deleteIntervalTaskScheduler() throws Exception {
        List<IntervalTaskScheduler> intervalTasks = testIntervalTaskGroup1.getIntervalTasks();
        intervalTasks.add(testIntervalTask1);
        intervalTasks.add(testIntervalTask2);
        testIntervalTaskGroup1.setIntervalTasks(intervalTasks);
        testIntervalTaskGroup1.generateTemplateSelectorString();
        when(intervalTaskGroupService.getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup1);
        when(intervalTaskGroupService.getIntervalTask(anyLong()))
                .thenReturn(testIntervalTask2);
        when(intervalTaskGroupService
                .saveIntervalTaskGroup(any(IntervalTaskGroup.class)))
                .thenReturn(testIntervalTaskGroup1);
        mockMvc.
                perform(request(
                                HttpMethod.DELETE,
                        "/api/interval/delete-scheduler/" +
                                testIntervalTask2.getId() + "/" +
                                testIntervalTaskGroup1.getId()
                        )
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("id")
                        .value(
                                testIntervalTaskGroup1.getId()
                        ))
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
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void deleteIntervalTaskSchedulerFailureSchedulerNonExistent()
        throws Exception {
        long nonExistentID = 12920L;
        when(intervalTaskGroupService.getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup1);
        mockMvc.
                perform(request(
                                HttpMethod.DELETE,
                        "/api/interval/delete-scheduler/" +
                                nonExistentID + "/" +
                                testIntervalTaskGroup1.getId()
                        )
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("message")
                        .value(
                                "There was an error. Please try again"
                        )
                );
    }

    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void deleteIntervalTaskSchedulerFailureGroupNonExistent()
            throws Exception {
        long nonExistentID = 12920L;
        when(intervalTaskGroupService.getIntervalTask(anyLong()))
                .thenReturn(testIntervalTask2);
        mockMvc.
                perform(request(
                                HttpMethod.DELETE,
                                "/api/interval/delete-scheduler/" +
                                        testIntervalTask2.getId() + "/"
                                        + nonExistentID

                        )
                                .with(csrf())
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("message")
                                .value(
                                        "The interval task group or task does not exist. Please try again"
                                )
                );
    }


    @Test
    @WithMockUser(authorities = {"USER", }, username = "TestUser")
    void getUsersIntervalTaskGroups() throws Exception {
        List<IntervalTaskGroup> usersIntervalTaskGroups = new ArrayList<>();
        testIntervalTaskGroup1.generateTemplateSelectorString();
        testIntervalTaskGroup2.generateTemplateSelectorString();
        usersIntervalTaskGroups.add(testIntervalTaskGroup1);
        usersIntervalTaskGroups.add(testIntervalTaskGroup2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(intervalTaskGroupService
                .getAllUsersIntervalTaskGroups(anyString()))
                .thenReturn(usersIntervalTaskGroups);
        mockMvc.perform(get("/api/interval/groups")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]taskGroupName")
                        .value(
                                testIntervalTaskGroupRequest.getTaskGroupName())
                )
                .andExpect(jsonPath("$[0]templateSelectorString").value(
                        "Test Interval Task Group 1 (Every 3 days)")
                )
                .andExpect(jsonPath("$[0]intervalInDays")
                        .value(testIntervalTaskGroupRequest.getIntervalInDays())
                )
                .andExpect(jsonPath("$[1]taskGroupName")
                        .value(
                                testIntervalTaskGroup2.getTaskGroupName())
                )
                .andExpect(jsonPath("$[1]templateSelectorString").value(
                        "Test Interval Task Group 2 (Every 3 days)")
                )
                .andExpect(jsonPath("$[1]intervalInDays")
                        .value(testIntervalTaskGroup2.getIntervalInDays())
                );
    }
}