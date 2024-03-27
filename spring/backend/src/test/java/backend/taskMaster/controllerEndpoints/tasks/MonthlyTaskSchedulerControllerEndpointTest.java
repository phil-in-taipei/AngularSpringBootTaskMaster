package backend.taskMaster.controllerEndpoints.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.repositories.tasks.taskSchedulers.MonthlyTaskSchedulerRepo;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TaskMasterApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class MonthlyTaskSchedulerControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;

    MonthlySchedulerPostRequest testMonthlyTaskSchedulerRequest = MonthlySchedulerPostRequest
            .builder()
            .monthlyTaskName("Test Monthly Task Scheduler 1")
            .dayOfMonth(1)
            .build();

    MonthlySchedulerPostRequest testMonthlyTaskSchedulerRequest2 = MonthlySchedulerPostRequest
            .builder()
            .monthlyTaskName("Test Monthly Task Scheduler 2")
            .dayOfMonth(2)
            .build();

    @Test
    @Order(1)
    @WithUserDetails("TestUser")
    void createMonthlyTaskScheduler() throws Exception {
        mockMvc.perform(post("/api/monthly/create")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                testMonthlyTaskSchedulerRequest)
                        )
                )
                .andDo(print())
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
        mockMvc.perform(post("/api/monthly/create")
                .contentType("application/json")
                .with(csrf())
                .content(TestUtil.convertObjectToJsonBytes(testMonthlyTaskSchedulerRequest2))
        );
    }

    @Test
    @Order(2)
    @WithUserDetails("TestUser")
    void getUsersMonthlyTaskSchedulers() throws Exception {
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
                                testMonthlyTaskSchedulerRequest2.getMonthlyTaskName())
                )
                .andExpect(jsonPath("$[1]templateSelectorString").value(
                        "Test Monthly Task Scheduler 2: 2nd day of month ")
                )
                .andExpect(jsonPath("$[1]dayOfMonth")
                        .value(testMonthlyTaskSchedulerRequest2.getDayOfMonth())
                );
    }
}
