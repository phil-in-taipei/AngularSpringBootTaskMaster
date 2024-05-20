package backend.taskMaster.controllerEndpoints.tasks;
import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.apiData.WeeklySchedulerPostRequest;
import backend.taskMaster.models.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.taskSchedulers.WeeklyTaskScheduler;
import backend.taskMaster.models.tasks.apiData.RecurringTaskAppliedQuarterlyPostRequest;
import backend.taskMaster.repositories.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.WeeklyTaskSchedulerRepo;
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
public class WeeklyTaskSchedulerControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WeeklyTaskSchedulerRepo weeklyTaskSchedulerRepo;

    @Autowired
    WeeklyTaskAppliedQuarterlyRepo weeklyTaskAppliedQuarterlyRepo;

    WeeklySchedulerPostRequest testWeeklyTaskSchedulerRequest = WeeklySchedulerPostRequest
            .builder()
            .weeklyTaskName("Test Weekly Task Scheduler 1")
            .dayOfWeek("MONDAY")
            .build();

    WeeklySchedulerPostRequest testWeeklyTaskSchedulerRequest2 = WeeklySchedulerPostRequest
            .builder()
            .weeklyTaskName("Test Weekly Task Scheduler 2")
            .dayOfWeek("TUESDAY")
            .build();

    @Test
    @Order(1)
    @WithUserDetails("TestUser")
    void createWeeklyTaskScheduler() throws Exception {
        mockMvc.perform(post("/api/weekly/create")
                        .contentType("application/json")
                        .with(csrf())
                        .content(TestUtil.convertObjectToJsonBytes(
                                testWeeklyTaskSchedulerRequest)
                        )
                )
                // .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("weeklyTaskName")
                        .value(
                                testWeeklyTaskSchedulerRequest.getWeeklyTaskName())
                )
                .andExpect(jsonPath("templateSelectorString").value(
                        "Test Weekly Task Scheduler 1 (every Monday)")
                )
                .andExpect(jsonPath("dayOfWeek")
                        .value(testWeeklyTaskSchedulerRequest.getDayOfWeek())
                );
        mockMvc.perform(post("/api/weekly/create")
                .contentType("application/json")
                .with(csrf())
                .content(TestUtil.convertObjectToJsonBytes(testWeeklyTaskSchedulerRequest2))
        );
    }

    @Test
    @Order(2)
    @WithUserDetails("TestUser")
    void getUsersWeeklyTaskSchedulers() throws Exception {
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
                                testWeeklyTaskSchedulerRequest.getWeeklyTaskName())
                )
                .andExpect(jsonPath("$[0]templateSelectorString").value(
                        "Test Weekly Task Scheduler 1 (every Monday)")
                )
                .andExpect(jsonPath("$[0]dayOfWeek")
                        .value(testWeeklyTaskSchedulerRequest.getDayOfWeek())
                )
                .andExpect(jsonPath("$[1]weeklyTaskName")
                        .value(
                                testWeeklyTaskSchedulerRequest2.getWeeklyTaskName())
                )
                .andExpect(jsonPath("$[1]templateSelectorString").value(
                        "Test Weekly Task Scheduler 2 (every Tuesday)")
                )
                .andExpect(jsonPath("$[1]dayOfWeek")
                        .value(testWeeklyTaskSchedulerRequest2.getDayOfWeek())
                );
    }

    // this will test saving a weekly task scheduler applied quarterly. It will apply the weekly
    // task scheduler created in the testSaveNewWeeklyTaskScheduler and apply it to the first
    // quarter of the current year
    @Test
    @Order(3)
    @WithUserDetails("TestUser")
    public void testSaveNewQuarterlyWeeklyTask() throws Exception  {
        WeeklyTaskScheduler testWeeklyTaskScheduler = weeklyTaskSchedulerRepo.findAll().get(0);
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        RecurringTaskAppliedQuarterlyPostRequest
                recurringTaskAppliedQuarterlyPostRequest = RecurringTaskAppliedQuarterlyPostRequest
                .builder()
                .year(thisYear)
                .quarter(quarter)
                .recurringTaskSchedulerId(testWeeklyTaskScheduler.getId())
                .build();
        mockMvc.perform(post("/api/weekly/apply-quarterly")
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
    @Order(4)
    @WithUserDetails("TestUser")
    void getUsersWeeklyTaskSchedulersAppliedQuarterlyByQuarterAndYear()
            throws Exception
    {
        WeeklyTaskScheduler testWeeklyTaskScheduler = weeklyTaskSchedulerRepo.findAll().get(0);
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        mockMvc.perform(get("/api/weekly/applied-quarterly/" + quarter + "/" + thisYear)
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]weeklyTaskSchedulerId")
                        .value(
                                testWeeklyTaskScheduler.getId())
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
    public void deleteWeeklyTaskScheduler() throws Exception {
        WeeklyTaskScheduler testWeeklyTaskScheduler = weeklyTaskSchedulerRepo.findAll().get(1);
        mockMvc.
                perform(request(
                        HttpMethod.DELETE, "/api/weekly/delete/" +
                                testWeeklyTaskScheduler.getId())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                testWeeklyTaskScheduler.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                "Weekly task scheduler successfully deleted!"
                        )
                );
    }

    @Test
    @Order(6)
    @WithUserDetails("TestUser")
    public void deleteWeeklyTaskSchedulerFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                        HttpMethod.DELETE, "/api/weekly/delete/" +
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
    public void deleteWeeklyTaskAppliedQuarterly() throws Exception {
        WeeklyTaskAppliedQuarterly
                testWeeklyTaskAppliedQuarterly = weeklyTaskAppliedQuarterlyRepo
                .findAll().get(0);
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/weekly/delete-applied-quarterly/" +
                                testWeeklyTaskAppliedQuarterly.getId())
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id")
                        .value(
                                testWeeklyTaskAppliedQuarterly.getId()
                        ))
                .andExpect(jsonPath("message")
                        .value(
                                "Weekly task application successfully deleted!"
                        )
                );
    }

    @Test
    @Order(8)
    @WithUserDetails("TestUser")
    public void deleteWeeklyTaskAppliedQuarterlyFailure() throws Exception {
        long nonExistentID = 12920L;
        mockMvc.
                perform(request(
                        HttpMethod.DELETE,
                        "/api/weekly/delete-applied-quarterly/" +
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
