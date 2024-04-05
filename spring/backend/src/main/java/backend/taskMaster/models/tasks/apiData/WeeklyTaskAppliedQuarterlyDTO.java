package backend.taskMaster.models.tasks.apiData;

import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeeklyTaskAppliedQuarterlyDTO {
    private Long id;
    private QuarterlySchedulingEnum quarter;
    private Integer year;
    private Long weeklyTaskSchedulerId;
}
