package backend.taskMaster.repositories.tasks.taskSchedulers;

import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervalTaskGroupRepo
        extends JpaRepository<IntervalTaskGroup, Long>{

    List<IntervalTaskGroup> findAllByTaskGroupOwnerUsername(
            String username
    );
}
