package backend.taskMaster.models.tasks.taskSchedulers;

import backend.taskMaster.models.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntervalTaskGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String taskGroupName;


    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn
    private User taskGroupOwner;

    // orphanRemoval deletes related interval tasks in the group upon deletion
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "interval_task_group_id")
    private List<IntervalTaskScheduler> intervalTasks;

    @Column(nullable = false)
    private int intervalInDays;

    public IntervalTaskGroup(
            String taskGroupName,
            int intervalInDays,
            User taskGroupOwner
    ) {
        this.taskGroupName = taskGroupName;
        this.intervalInDays = intervalInDays;
        this.taskGroupOwner = taskGroupOwner;
    }

    @Transient
    private String templateSelectorString;

    // this is for forms in frontend to have a readable String
    public void generateTemplateSelectorString() {
        this.templateSelectorString = taskGroupName + " "
                + "(Every " +
                intervalInDays + " days)";
    }

}
