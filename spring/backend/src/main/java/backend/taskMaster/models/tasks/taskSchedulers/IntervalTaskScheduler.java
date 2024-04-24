package backend.taskMaster.models.tasks.taskSchedulers;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntervalTaskScheduler {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String intervalTaskName;

    public IntervalTaskScheduler(String intervalTaskName) {
        this.intervalTaskName = intervalTaskName;
    }
}
