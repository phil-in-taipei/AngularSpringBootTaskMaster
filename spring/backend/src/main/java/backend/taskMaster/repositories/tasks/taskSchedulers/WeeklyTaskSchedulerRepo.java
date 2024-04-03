package backend.taskMaster.repositories.tasks.taskSchedulers;
import backend.taskMaster.models.tasks.taskSchedulers.WeeklyTaskScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyTaskSchedulerRepo
        extends JpaRepository<WeeklyTaskScheduler, Long> {

    List<WeeklyTaskScheduler> findAllByUserUsernameOrderByDayOfWeekAsc(
            String username
    );
}
