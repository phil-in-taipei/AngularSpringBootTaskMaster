package backend.taskMaster.models.tasks.apiData;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySchedulerPostRequest {
    private String monthlyTaskName;
    private Integer dayOfMonth;;
}
