package backend.taskMaster.services.utils;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TaskMasterApplication.class)
@ActiveProfiles("test")
class GenerateTaskBatchesServiceTest {

    @Autowired
    GenerateTaskBatchesService generateTaskBatchesService;

    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();
    @Test
    void generateRecurringTasksByDateList() {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(10).withYear(2024));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2024));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(10).withYear(2024));

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                                "Test Task", testUser, dates)
                        .size())
                .isEqualTo(3);
        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(0).getTaskName()
        ).isEqualTo("Test Task");

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(0).getDate()
        ).isEqualTo(LocalDate.now().withMonth(4).withDayOfMonth(10).withYear(2024));

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(1).getTaskName()
        ).isEqualTo("Test Task");

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(1).getDate()
        ).isEqualTo(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2024));

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(2).getTaskName()
        ).isEqualTo("Test Task");

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(2).getDate()
        ).isEqualTo(LocalDate.now().withMonth(6).withDayOfMonth(10).withYear(2024));
    }
}