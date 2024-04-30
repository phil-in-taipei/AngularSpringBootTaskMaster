package backend.taskMaster.models.tasks.apiData;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringTaskAppliedQuarterlyPostRequest {

    Long recurringTaskSchedulerId;

    String quarter;

    int year;
}
