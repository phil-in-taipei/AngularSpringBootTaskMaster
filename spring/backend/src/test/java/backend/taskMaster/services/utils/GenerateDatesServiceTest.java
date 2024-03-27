package backend.taskMaster.services.utils;

import backend.taskMaster.TaskMasterApplication;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = TaskMasterApplication.class)
@ActiveProfiles("test")
class GenerateDatesServiceTest {


    @Autowired
    GenerateDatesService generateDatesService;

    @Test
    void getMonthlySchedulingDatesByQuarter() {
        // 10th day of month in Q2 of 2023 should be the following:
        // 4/10, 5/10, 6/10
        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(10).withYear(2024));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2024));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(10).withYear(2024));

        assertThat(
                generateDatesService.getMonthlySchedulingDatesByQuarter(
                                2024, QuarterlySchedulingEnum.Q2, 10)
                        .size())
                .isEqualTo(3);
        assertThat(
                generateDatesService.getMonthlySchedulingDatesByQuarter(
                        2024, QuarterlySchedulingEnum.Q2, 10)
        ).isEqualTo(dates);
    }
}