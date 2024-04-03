package backend.taskMaster.models.tasks.appliedSchedulers;

import backend.taskMaster.models.tasks.taskSchedulers.WeeklyTaskScheduler;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames =
        {"quarter", "year", "weekly_task_scheduler_id"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyTaskAppliedQuarterly {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuarterlySchedulingEnum quarter;

    @Column(nullable = false)
    @NotNull(message = "A Year must be provided")
    @Min(
            value = 2023,
            message = "Prior to 2023 cannot be arranged"
    )
    @Max(
            value = 2025,
            message = "After 2025 cannot be arranged"
    )
    private Integer year;

    @ManyToOne(optional = false)
    private WeeklyTaskScheduler weeklyTaskScheduler;

    @Override
    public String toString() {
        return "WeeklyTaskAppliedQuarterly{" +
                "id=" + id +
                ", quarter=" + quarter +
                ", year=" + year +
                ", weeklyTaskScheduler=" + weeklyTaskScheduler +
                '}';
    }
}
