package backend.taskMaster.models.tasks.apiData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskPostRequest {
    private String taskName;
    private String description;
    private String date;
}
