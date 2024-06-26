package backend.taskMaster.models.tasks.appliedSchedulers;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames =
        {"quarter", "year", "monthly_task_scheduler_id"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTaskAppliedQuarterly {

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
    //@JsonIgnoreProperties(
    //        {"monthlyTaskName", "dayOfMonth", "templateSelectorString"}
    //)
    private MonthlyTaskScheduler monthlyTaskScheduler;

    @Override
    public String toString() {
        return "MonthlyTaskAppliedQuarterly{" +
                "id=" + id +
                ", quarter=" + quarter +
                ", year=" + year +
                ", monthlyTaskScheduler=" + monthlyTaskScheduler +
                '}';
    }

}
