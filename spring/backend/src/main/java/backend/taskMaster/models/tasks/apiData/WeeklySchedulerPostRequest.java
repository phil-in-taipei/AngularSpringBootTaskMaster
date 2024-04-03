package backend.taskMaster.models.tasks.apiData;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySchedulerPostRequest {

    private String weeklyTaskName;

    private String dayOfWeek;

}
