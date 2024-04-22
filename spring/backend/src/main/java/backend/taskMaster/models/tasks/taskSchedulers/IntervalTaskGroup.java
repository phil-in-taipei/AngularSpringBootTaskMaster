package backend.taskMaster.models.tasks.taskSchedulers;

import backend.taskMaster.models.user.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
    @JoinColumn
    private User taskGroupOwner;

    // orphanRemoval deletes related interval tasks in the group upon deletion
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "interval_task_group_id")
    private List<IntervalTaskScheduler> intervalTasks;

    @Column(nullable = false)
    private int intervalInDays;

    @Transient
    private String templateSelectorString;

    // this is for forms in frontend to have a readable String
    public void generateTemplateSelectorString() {
        this.templateSelectorString = taskGroupName + " "
                + "(Every " +
                intervalInDays + " days)";
    }

}
