package backend.taskMaster.models.tasks.apiData;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskPostRequest {
    private String taskName;
    private String date;
}
