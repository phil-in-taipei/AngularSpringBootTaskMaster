package backend.taskMaster.repositories.tasks.task;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.task.TaskStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EnumType;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SingleTaskRepo extends JpaRepository<SingleTask, Long> {

    List<SingleTask> findAllByUserUsernameAndDateBetweenOrderByDateAsc(
            String username, LocalDate firstDate, LocalDate lastDate
    );

    List<SingleTask> findByStatusIsNotAndDateBeforeAndUserUsername(
            TaskStatusEnum status, LocalDate date, String username
    );

    List<SingleTask> findAllByUserUsernameAndDate(String id, LocalDate date);

}
