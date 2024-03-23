package backend.taskMaster.controllerEndpoints.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.TaskPostRequest;
import backend.taskMaster.models.tasks.apiData.TaskReschedulePatchRequest;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.repositories.tasks.task.SingleTaskRepo;
import backend.taskMaster.utils.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TaskMasterApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class BasicTaskControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SingleTaskRepo singleTaskRepo;


    TaskPostRequest testCreateRequest = TaskPostRequest.builder()
            .taskName("Test Task 1")
            .date(LocalDate.now().minusDays(1).toString())
            .build();


    TaskPostRequest testCreateRequest2 = TaskPostRequest.builder()
            .taskName("Test Task 2")
            .date(LocalDate.now().toString())
            .build();

    TaskReschedulePatchRequest reschedulePatchRequest = TaskReschedulePatchRequest
            .builder()
            .date(LocalDate.now().toString())
            .comments("Rescheduling")
            .build();

    @Test
    @Order(1)
    @WithUserDetails("TestUser")
    void createTask() throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
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

        mockMvc.perform(post("/api/task/create")
                .contentType("application/json")
                .with(csrf())
                .content(TestUtil.convertObjectToJsonBytes(testCreateRequest2))
        );
    }

    @Test
    @Order(4)
    @WithUserDetails("TestUser")
    void getUserTasksForCurrentMonth() throws Exception {
        SingleTask testTask = singleTaskRepo.findAll().get(0);
        SingleTask testTask2 = singleTaskRepo.findAll().get(1);
        mockMvc.perform(get("/api/task/current-month")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].taskName").value(testTask.getTaskName()))
                .andExpect(jsonPath("$[1].id").value(testTask2.getId()))
                .andExpect(jsonPath("$[1].taskName").value(testTask2.getTaskName()));
    }

    @Test
    @Order(3)
    @WithUserDetails("TestUser")
    void rescheduleTask() throws Exception {
        LocalDate today = LocalDate.now();
        SingleTask testTask = singleTaskRepo.findAll().get(0);
        mockMvc.perform(patch("/api/task/reschedule/" + testTask.getId())
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(reschedulePatchRequest))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]taskName")
                        .value(
                                "Test Task 1")
                )
                .andExpect(jsonPath("$[0]date")
                        .value(
                                today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                );
    }

    @Test
    @Order(6)
    @WithUserDetails("TestUser")
    void rescheduleTaskFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.perform(patch("/api/task/reschedule/" + nonExistentID)
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(reschedulePatchRequest))
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value("There was an error. Please try again")
                );
    }

    @Test
    @Order(2)
    @WithUserDetails("TestUser")
    void getUserTasksByDate() throws Exception {
        SingleTask testTask = singleTaskRepo.findAll().get(0);
        String yesterday = LocalDate.now().minusDays(1).toString();
        mockMvc.perform(get("/api/task/date/" + yesterday))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].taskName").value(testTask.getTaskName()));
    }

    @Test
    @Order(5)
    @WithUserDetails("TestUser")
    void getUserTasksByMonthAndYear() throws Exception {
        SingleTask testTask = singleTaskRepo.findAll().get(0);
        SingleTask testTask2 = singleTaskRepo.findAll().get(1);
        LocalDate today = LocalDate.now();
        int thisMonthInt = today.getMonthValue();
        int thisYearInt = today.getYear();
        mockMvc
                .perform(get("/api/task/month-year/"
                        + String.valueOf(thisMonthInt) + "/"
                        + String.valueOf(thisYearInt)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$[0].taskName").value(testTask.getTaskName()))
                .andExpect(jsonPath("$[1].id").value(testTask2.getId()))
                .andExpect(jsonPath("$[1].taskName").value(testTask2.getTaskName()));
    }

    @Test
    @Order(8)
    @WithUserDetails("TestUser")
    public void deleteTask() throws Exception {
        SingleTask testTask2 = singleTaskRepo.findAll().get(1);
        mockMvc.
                perform(request(HttpMethod.GET, "/api/task/delete/" + testTask2.getId()))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                testTask2.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                "Task successfully deleted!"
                        )
                );
    }

    @Test
    @Order(7)
    @WithUserDetails("TestUser")
    public void deleteTaskFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(HttpMethod.GET, "/api/task/delete/" + nonExistentID))
                //.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message")
                        .value(
                                "Deletion Failed. Item not found"
                        )
                );
    }
}
