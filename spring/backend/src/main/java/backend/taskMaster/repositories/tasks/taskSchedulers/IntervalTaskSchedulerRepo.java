package backend.taskMaster.repositories.tasks.taskSchedulers;

import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntervalTaskSchedulerRepo
        extends JpaRepository<IntervalTaskScheduler, Long> {
}
