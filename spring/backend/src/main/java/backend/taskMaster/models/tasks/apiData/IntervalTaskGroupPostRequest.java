package backend.taskMaster.models.tasks.apiData;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntervalTaskGroupPostRequest {

    private String taskGroupName;

    private int intervalInDays;
}
