package backend.taskMaster.models.tasks.appliedSchedulers;

import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames =
        {"quarter", "year", "interval_task_group_id"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntervalTaskGroupAppliedQuarterly {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuarterlySchedulingEnum quarter;

    @Column(nullable = false)
    @NotNull(message = "A Year must be provided")
    @Min(
            value = 2024,
            message = "Prior to 2023 cannot be arranged"
    )
    @Max(
            value = 2026,
            message = "After 2025 cannot be arranged"
    )
    private Integer year;

    @ManyToOne(optional = false)
    private IntervalTaskGroup intervalTaskGroup;
}
