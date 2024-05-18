package backend.taskMaster.controllerEndpoints.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.MonthlySchedulerPostRequest;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.tasks.apiData.RecurringTaskAppliedQuarterlyPostRequest;
import backend.taskMaster.repositories.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.MonthlyTaskSchedulerRepo;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    MonthlyTaskAppliedQuarterlyRepo monthlyTaskAppliedQuarterlyRepo;

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
               // .andDo(print())
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
                                testMonthlyTaskSchedulerRequest2.getMonthlyTaskName())
                )
                .andExpect(jsonPath("$[1]templateSelectorString").value(
                        "Test Monthly Task Scheduler 2: 2nd day of month ")
                )
                .andExpect(jsonPath("$[1]dayOfMonth")
                        .value(testMonthlyTaskSchedulerRequest2.getDayOfMonth())
                );
    }

    // this will test saving a monthly task scheduler applied quarterly. It will apply the monthly
    // task scheduler created in the testSaveNewMonthlyTaskScheduler and apply it to the first
    // quarter of the current year
    @Test
    @Order(3)
    @WithUserDetails("TestUser")
    public void testSaveNewQuarterlyMonthlyTask() throws Exception  {
        MonthlyTaskScheduler testMonthlyTaskScheduler = monthlyTaskSchedulerRepo.findAll().get(0);
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        RecurringTaskAppliedQuarterlyPostRequest
                recurringTaskAppliedQuarterlyPostRequest = RecurringTaskAppliedQuarterlyPostRequest
                .builder()
                .year(thisYear)
                .quarter(quarter)
                .recurringTaskSchedulerId(testMonthlyTaskScheduler.getId())
                .build();
        mockMvc.perform(post("/api/monthly/apply-quarterly")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                recurringTaskAppliedQuarterlyPostRequest)
                        )
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
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
    @Order(4)
    @WithUserDetails("TestUser")
    void getUsersMonthlyTaskSchedulersAppliedQuarterlyByQuarterAndYear()
            throws Exception
    {
        MonthlyTaskScheduler testMonthlyTaskScheduler = monthlyTaskSchedulerRepo.findAll().get(0);
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        mockMvc.perform(get("/api/monthly/applied-quarterly/" + quarter + "/" + thisYear)
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]monthlyTaskSchedulerId")
                        .value(
                                testMonthlyTaskScheduler.getId())
                )
                .andExpect(jsonPath("$[0]quarter").value(
                        quarter)
                )
                .andExpect(jsonPath("$[0]year")
                        .value(thisYear
                ));

    }

    @Test
    @Order(5)
    @WithUserDetails("TestUser")
    public void deleteMonthlyTaskScheduler() throws Exception {
        MonthlyTaskScheduler testMonthlyTaskScheduler = monthlyTaskSchedulerRepo.findAll().get(1);
        mockMvc.
                perform(request(
                        HttpMethod.DELETE, "/api/monthly/delete/" +
                                testMonthlyTaskScheduler.getId())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                testMonthlyTaskScheduler.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                "Monthly task scheduler successfully deleted!"
                        )
                );
    }

    @Test
    @Order(6)
    @WithUserDetails("TestUser")
    public void deleteMonthlyTaskSchedulerFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                        HttpMethod.DELETE, "/api/monthly/delete/" +
                                nonExistentID)
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
    @Order(7)
    @WithUserDetails("TestUser")
    public void deleteMonthlyTaskAppliedQuarterly() throws Exception {
        MonthlyTaskAppliedQuarterly
                testMonthlyTaskAppliedQuarterly = monthlyTaskAppliedQuarterlyRepo
                                                    .findAll().get(0);
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/monthly/delete-applied-quarterly/" +
                                testMonthlyTaskAppliedQuarterly.getId())
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
    @Order(8)
    @WithUserDetails("TestUser")
    public void deleteMonthlyTaskAppliedQuarterlyFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/monthly/delete-applied-quarterly/" +
                                nonExistentID)
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
