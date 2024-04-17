package backend.taskMaster.services.tasks;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.taskSchedulers.WeeklyTaskScheduler;
import backend.taskMaster.models.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterly;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.task.SingleTaskRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.WeeklyTaskSchedulerRepo;
import backend.taskMaster.services.user.UserDetailsServiceImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= TaskMasterApplication.class)
@ActiveProfiles("test")
class WeeklyTaskSchedulingServiceTest {

    @MockBean
    SingleTaskRepo singleTaskRepo;

    @MockBean
    UserDetailsServiceImplementation userService;

    @MockBean
    WeeklyTaskAppliedQuarterlyRepo weeklyTaskAppliedQuarterlyRepo;

    @MockBean
    WeeklyTaskSchedulerRepo weeklyTaskSchedulerRepo;

    @Autowired
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
    void getAllUsersWeeklyTaskSchedulers() {
        List<WeeklyTaskScheduler> userWeeklyTaskSchedulers = new ArrayList<>();
        userWeeklyTaskSchedulers.add(weeklyTaskScheduler);
        userWeeklyTaskSchedulers.add(weeklyTaskScheduler2);
        when(weeklyTaskSchedulerRepo.findAllByUserUsernameOrderByDayOfWeekAsc(anyString()))
                .thenReturn(userWeeklyTaskSchedulers);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTaskSchedulers("testuser"))
                .isEqualTo(userWeeklyTaskSchedulers);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTaskSchedulers("testuser").size())
                .isEqualTo(userWeeklyTaskSchedulers.size());
    }

    @Test
    void getAllUsersWeeklyTasksAppliedQuarterly() {
        List<WeeklyTaskAppliedQuarterly> usersWeeklyTasksAppliedQuarterly = new ArrayList<>();
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly);
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly2);
        when(weeklyTaskAppliedQuarterlyRepo
                .findAllByWeeklyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(
                        anyString()))
                .thenReturn(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTasksAppliedQuarterly("testuser"))
                .isEqualTo(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTasksAppliedQuarterly("testuser").size())
                .isEqualTo(usersWeeklyTasksAppliedQuarterly.size());
    }

    @Test
    void getAllUsersWeeklyTaskSchedulersAvailableForQuarterAndYear() {
    }

    @Test
    void getAllWeeklyTasksAlreadyScheduledForQuarterAndYear() {
    }

    @Test
    void getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear() {
        List<WeeklyTaskAppliedQuarterly> usersWeeklyTasksAppliedQuarterly = new ArrayList<>();
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly);
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly2);
        when(weeklyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndWeeklyTaskScheduler_UserUsername(
                        eq(QuarterlySchedulingEnum.Q2), eq(2023), anyString()))
                .thenReturn(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser"))
                .isEqualTo(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser").size())
                .isEqualTo(usersWeeklyTasksAppliedQuarterly.size());
    }

    @Test
    void getWeeklyTaskScheduler() {
        when(weeklyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.of(weeklyTaskScheduler));
        assertThat(weeklyTaskSchedulingService.getWeeklyTaskScheduler(1L))
                .isEqualTo(weeklyTaskScheduler);
    }

    @Test
    void getWeeklyTaskSchedulerFailure() {
        when(weeklyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(weeklyTaskSchedulingService.getWeeklyTaskScheduler(2L))
                .isEqualTo(null);
    }

    @Test
    void getWeeklyTaskAppliedQuarterly() {
        when(weeklyTaskAppliedQuarterlyRepo.findById(anyLong()))
                .thenReturn(Optional.of(testWeeklyTaskAppliedQuarterly));
        assertThat(weeklyTaskSchedulingService.getWeeklyTaskAppliedQuarterly(1L))
                .isEqualTo(testWeeklyTaskAppliedQuarterly);
    }

    @Test
    void getWeeklyTaskAppliedQuarterlyFailure() {
        when(weeklyTaskAppliedQuarterlyRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(weeklyTaskSchedulingService.getWeeklyTaskAppliedQuarterly(2L))
                .isEqualTo(null);
    }

    @Test
    void saveWeeklyTaskScheduler() {
        when(weeklyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.of(weeklyTaskScheduler));
        assertThat(weeklyTaskSchedulingService.getWeeklyTaskScheduler(1L))
                .isEqualTo(weeklyTaskScheduler);
    }

    @Test
    void saveWeeklyTaskSchedulerFailure() {
        when(weeklyTaskSchedulerRepo.save(any(WeeklyTaskScheduler.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            weeklyTaskSchedulingService
                    .saveWeeklyTaskScheduler(weeklyTaskScheduler);
        });
    }

    @Test
    void saveWeeklyTaskAppliedQuarterly() {
        when(weeklyTaskAppliedQuarterlyRepo.save(
                any(WeeklyTaskAppliedQuarterly.class)))
                .thenReturn(testWeeklyTaskAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly))
                .isEqualTo(testWeeklyTaskAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly).getYear())
                .isEqualTo(2023);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly).getQuarter())
                .isEqualTo(QuarterlySchedulingEnum.Q2);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly)
                .getWeeklyTaskScheduler().getWeeklyTaskName())
                .isEqualTo("Test Weekly Task Scheduler");
    }

    @Test
    void saveWeeklyTaskAppliedQuarterlyFailure() {
        when(weeklyTaskAppliedQuarterlyRepo.save(
                any(WeeklyTaskAppliedQuarterly.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            weeklyTaskSchedulingService
                    .saveWeeklyTaskAppliedQuarterly(
                            testWeeklyTaskAppliedQuarterly
                    );
        });
    }
}