package backend.taskMaster.services.utils;

import backend.taskMaster.logging.Loggable;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;


@Service
public class GenerateDatesService {

    @Loggable
    public List<LocalDate> getMonthlySchedulingDatesByQuarter(
            Integer year,
            QuarterlySchedulingEnum quarter,
            Integer dayOfMonth
    ) {
        // this adds the dates to an ArrayList (one per month at the desired day of month)
        List<LocalDate> dates = new ArrayList<>();
        switch (quarter) {
            case Q1:
                //System.out.println("Quarter 1");
                dates.add(LocalDate.of(year, 1, dayOfMonth));
                dates.add(LocalDate.of(year, 2, dayOfMonth));
                dates.add(LocalDate.of(year, 3, dayOfMonth));
                break;
            case Q2:
                //System.out.println("Quarter 2");
                dates.add(LocalDate.of(year, 4, dayOfMonth));
                dates.add(LocalDate.of(year, 5, dayOfMonth));
                dates.add(LocalDate.of(year, 6, dayOfMonth));
                break;
            case Q3:
                //System.out.println("Quarter 3");
                dates.add(LocalDate.of(year, 7, dayOfMonth));
                dates.add(LocalDate.of(year, 8, dayOfMonth));
                dates.add(LocalDate.of(year, 9, dayOfMonth));
                break;
            default:
                //System.out.println("Quarter 4");
                dates.add(LocalDate.of(year, 10, dayOfMonth));
                dates.add(LocalDate.of(year, 11, dayOfMonth));
                dates.add(LocalDate.of(year, 12, dayOfMonth));
                break;
        }
        return dates;
    }
}
