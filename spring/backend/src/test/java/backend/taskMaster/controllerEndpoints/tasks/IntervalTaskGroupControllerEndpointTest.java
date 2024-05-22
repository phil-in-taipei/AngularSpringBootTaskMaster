package backend.taskMaster.controllerEndpoints.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.IntervalTaskGroupPostRequest;
import backend.taskMaster.models.tasks.apiData.IntervalTaskSchedulerPostRequest;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;
import backend.taskMaster.repositories.tasks.appliedSchedulers.IntervalTaskGroupAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.IntervalTaskGroupRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.IntervalTaskSchedulerRepo;
import backend.taskMaster.utils.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TaskMasterApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class IntervalTaskGroupControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    IntervalTaskGroupRepo intervalTaskGroupRepo;

    @Autowired
    IntervalTaskGroupAppliedQuarterlyRepo intervalTaskGroupAppliedQuarterlyRepo;

    @Autowired
    IntervalTaskSchedulerRepo intervalTaskSchedulerRepo;

    IntervalTaskGroupPostRequest
            testIntervalTaskGroupRequest1 = IntervalTaskGroupPostRequest
            .builder()
            .taskGroupName("Test Interval Task Group 1")
            .intervalInDays(3)
            .build();

    IntervalTaskGroupPostRequest
            testIntervalTaskGroupRequest2 = IntervalTaskGroupPostRequest
            .builder()
            .taskGroupName("Test Interval Task Group 2")
            .intervalInDays(3)
            .build();

    IntervalTaskSchedulerPostRequest
            intervalTaskSchedulerPostRequest = IntervalTaskSchedulerPostRequest
            .builder()
            .intervalTaskGroupId(1L)
            .intervalTaskName("Test Interval Task 1")
            .build();

    @Test
    @Order(1)
    @WithUserDetails("TestUser")
    void createIntervalTaskGroup() throws Exception {
        mockMvc.perform(post("/api/interval/create-group")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                testIntervalTaskGroupRequest1)
                        )
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("taskGroupName")
                        .value(
                                testIntervalTaskGroupRequest1.getTaskGroupName())
                )
                .andExpect(jsonPath("templateSelectorString").value(
                        "Test Interval Task Group 1 (Every 3 days)")
                )
                .andExpect(jsonPath("intervalInDays")
                        .value(testIntervalTaskGroupRequest1.getIntervalInDays())
                );
        mockMvc.perform(post("/api/interval/create-group")
                .contentType("application/json")
                .with(csrf())
                .content(TestUtil.convertObjectToJsonBytes(
                        testIntervalTaskGroupRequest2)
                )
        );
    }

    @Test
    @Order(2)
    @WithUserDetails("TestUser")
    void createIntervalTaskScheduler() throws Exception {
        IntervalTaskGroup testIntervalTaskGroup1 = intervalTaskGroupRepo.findAll().get(0);
        intervalTaskSchedulerPostRequest.setIntervalTaskGroupId(testIntervalTaskGroup1.getId());
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
                        .value(intervalTaskSchedulerPostRequest.getIntervalTaskName())
                );
    }
}
