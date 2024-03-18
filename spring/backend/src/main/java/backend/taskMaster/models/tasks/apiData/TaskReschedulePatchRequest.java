package backend.taskMaster.models.tasks.apiData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskReschedulePatchRequest {
    private String date;
    private String comments;
}
