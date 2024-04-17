package backend.taskMaster.services.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.task.SingleTaskRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.MonthlyTaskSchedulerRepo;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= TaskMasterApplication.class)
@ActiveProfiles("test")
class MonthlyTaskSchedulingServiceTest {

    @MockBean
    SingleTaskRepo singleTaskRepo;

    @MockBean
    UserDetailsServiceImplementation userService;

    @MockBean
    MonthlyTaskAppliedQuarterlyRepo monthlyTaskAppliedQuarterlyRepo;

    @MockBean
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

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
            .monthlyTaskName("Test Monthly Task Scheduler")
            .dayOfMonth(1)
            .user(testUser)
            .build();

    MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
            MonthlyTaskAppliedQuarterly
                    .builder()
                    .monthlyTaskScheduler(testMonthlyTaskScheduler)
                    .year(2023)
                    .quarter(QuarterlySchedulingEnum.Q2)
                    .build();

    MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
            .id(2L)
            .monthlyTaskName("Test Monthly Task Scheduler")
            .dayOfMonth(1)
            .user(testUser)
            .build();

    MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly2 =
            MonthlyTaskAppliedQuarterly
                    .builder()
                    .monthlyTaskScheduler(testMonthlyTaskScheduler2)
                    .year(2023)
                    .quarter(QuarterlySchedulingEnum.Q2)
                    .build();

    @Test
    void getAllUsersMonthlyTaskSchedulers() {
        List<MonthlyTaskScheduler> usersMonthlyTaskSchedulers = new ArrayList<>();
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler);
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler2);
        when(monthlyTaskSchedulerRepo.
                findAllByUserUsernameOrderByDayOfMonthAsc(anyString()))
                .thenReturn(usersMonthlyTaskSchedulers);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulers("testuser"))
                .isEqualTo(usersMonthlyTaskSchedulers);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulers("testuser").size())
                .isEqualTo(usersMonthlyTaskSchedulers.size());
    }

    @Test
    void getAllUsersMonthlyTasksAppliedQuarterly() {
        List<MonthlyTaskAppliedQuarterly> usersMonthlyTasksAppliedQuarterly = new ArrayList<>();
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly);
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly2);
        when(monthlyTaskAppliedQuarterlyRepo
                .findAllByMonthlyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(anyString()))
                .thenReturn(usersMonthlyTasksAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTasksAppliedQuarterly("testuser"))
                .isEqualTo(usersMonthlyTasksAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTasksAppliedQuarterly("testuser").size())
                .isEqualTo(usersMonthlyTasksAppliedQuarterly.size());
    }

    @Test
    void getMonthlyTaskScheduler() {
        when(monthlyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.of(testMonthlyTaskScheduler));
        assertThat(monthlyTaskSchedulingService.getMonthlyTaskScheduler(1L))
                .isEqualTo(testMonthlyTaskScheduler);
    }

    @Test
    void getMonthlyTaskSchedulerFailure() {
        when(monthlyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(monthlyTaskSchedulingService.getMonthlyTaskScheduler(2L))
                .isEqualTo(null);
    }

    @Test
    void getMonthlyTaskAppliedQuarterly() {
        when(monthlyTaskAppliedQuarterlyRepo.save(
                any(MonthlyTaskAppliedQuarterly.class)))
                .thenReturn(testMonthlyTaskAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly))
                .isEqualTo(testMonthlyTaskAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getYear())
                .isEqualTo(2023);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getQuarter())
                .isEqualTo(QuarterlySchedulingEnum.Q2);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getMonthlyTaskScheduler()
                .getMonthlyTaskName())
                .isEqualTo("Test Monthly Task Scheduler");
    }

    @Test
    void getMonthlyTaskAppliedQuarterlyFailure() {
        when(monthlyTaskAppliedQuarterlyRepo.save(
                any(MonthlyTaskAppliedQuarterly.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            monthlyTaskSchedulingService
                    .saveMonthlyTaskAppliedQuarterly(
                            testMonthlyTaskAppliedQuarterly);
        });
    }

    @Test
    void saveMonthlyTaskScheduler() {
        when(monthlyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.of(testMonthlyTaskScheduler));
        assertThat(monthlyTaskSchedulingService.getMonthlyTaskScheduler(1L))
                .isEqualTo(testMonthlyTaskScheduler);
    }

    @Test
    void saveMonthlyTaskSchedulerFailure() {
        when(monthlyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(monthlyTaskSchedulingService.getMonthlyTaskScheduler(2L))
                .isEqualTo(null);
    }

    @Test
    void saveMonthlyTaskAppliedQuarterly() {
        when(monthlyTaskAppliedQuarterlyRepo.save(
                any(MonthlyTaskAppliedQuarterly.class)))
                .thenReturn(testMonthlyTaskAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly))
                .isEqualTo(testMonthlyTaskAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getYear())
                .isEqualTo(2023);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getQuarter())
                .isEqualTo(QuarterlySchedulingEnum.Q2);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getMonthlyTaskScheduler()
                .getMonthlyTaskName())
                .isEqualTo("Test Monthly Task Scheduler");
    }

    @Test
    void saveMonthlyTaskAppliedQuarterlyFailure() {
        when(monthlyTaskAppliedQuarterlyRepo.save(
                any(MonthlyTaskAppliedQuarterly.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            monthlyTaskSchedulingService
                    .saveMonthlyTaskAppliedQuarterly(
                            testMonthlyTaskAppliedQuarterly);
        });
    }

}