package backend.taskMaster.models.tasks.apiData;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDeletionResponse {
    private Long id;
    private String message;
}
