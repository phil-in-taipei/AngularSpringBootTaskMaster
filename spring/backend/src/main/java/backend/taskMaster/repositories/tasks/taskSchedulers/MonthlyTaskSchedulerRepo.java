package backend.taskMaster.repositories.tasks.taskSchedulers;

import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyTaskSchedulerRepo
        extends JpaRepository<MonthlyTaskScheduler, Long> {

    List<MonthlyTaskScheduler> findAllByUserUsernameOrderByDayOfMonthAsc(
            String username);

}
