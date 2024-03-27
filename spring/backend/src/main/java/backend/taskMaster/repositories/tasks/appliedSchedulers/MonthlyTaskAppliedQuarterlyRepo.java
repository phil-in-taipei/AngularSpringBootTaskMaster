package backend.taskMaster.repositories.tasks.appliedSchedulers;

import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MonthlyTaskAppliedQuarterlyRepo
        extends JpaRepository<MonthlyTaskAppliedQuarterly, Long> {

    List<MonthlyTaskAppliedQuarterly>
        findAllByMonthlyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(
                String username
        );


    List<MonthlyTaskAppliedQuarterly>
        findAllByQuarterAndYearAndMonthlyTaskScheduler_UserUsername(
                QuarterlySchedulingEnum quarter, Integer year, String username
        );

}
