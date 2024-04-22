package backend.taskMaster.repositories.tasks.appliedSchedulers;

import backend.taskMaster.models.tasks.appliedSchedulers.IntervalTaskGroupAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervalTaskGroupAppliedQuarterlyRepo
        extends JpaRepository<IntervalTaskGroupAppliedQuarterly, Long>{

    List<IntervalTaskGroupAppliedQuarterly>
        findAllByIntervalTaskGroup_TaskGroupOwnerUsernameOrderByYearAscQuarterAsc(
                String username
        );

    List<IntervalTaskGroupAppliedQuarterly>
        findAllByQuarterAndYearAndIntervalTaskGroup_TaskGroupOwnerUsername(
                QuarterlySchedulingEnum quarter,
                Integer year, String username
    );

    List<IntervalTaskGroupAppliedQuarterly> findAllByIntervalTaskGroupId(
            Long id
    );
}
