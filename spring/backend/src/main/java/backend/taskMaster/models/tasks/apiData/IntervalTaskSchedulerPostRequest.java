package backend.taskMaster.models.tasks.apiData;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntervalTaskSchedulerPostRequest {

    private String intervalTaskName;

    private Long intervalTaskGroupId;

}
