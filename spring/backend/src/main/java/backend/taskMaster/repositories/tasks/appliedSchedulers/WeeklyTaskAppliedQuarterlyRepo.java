package backend.taskMaster.repositories.tasks.appliedSchedulers;

import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyTaskAppliedQuarterlyRepo
        extends JpaRepository<WeeklyTaskAppliedQuarterly, Long> {

    List<WeeklyTaskAppliedQuarterly>
    findAllByWeeklyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(
            String username
    );

    List<WeeklyTaskAppliedQuarterly>
    findAllByQuarterAndYearAndWeeklyTaskScheduler_UserUsername(
            QuarterlySchedulingEnum quarter,
            Integer year, String username
    );

}
