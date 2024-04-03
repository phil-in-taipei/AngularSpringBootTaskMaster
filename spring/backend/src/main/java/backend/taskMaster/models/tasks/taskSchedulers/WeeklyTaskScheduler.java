package backend.taskMaster.models.tasks.taskSchedulers;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;
import backend.taskMaster.models.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterly;
import backend.taskMaster.models.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyTaskScheduler {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String weeklyTaskName;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn
    private User user;

    // orphanRemoval deletes related quarterly applications
    // of the WeeklyTaskScheduler upon deletion
    @OneToMany(mappedBy = "weeklyTaskScheduler", orphanRemoval = true)
    private Set<WeeklyTaskAppliedQuarterly> weeklyTaskAppliedQuarterly;

    public WeeklyTaskScheduler(
            String weeklyTaskName,
            String dayOfWeek,
            User user
    ) {
        this.weeklyTaskName = weeklyTaskName;
        this.dayOfWeek = DayOfWeek.valueOf(dayOfWeek);
        this.user = user;
    }

    @Override
    public String toString() {
        return "WeeklyTaskScheduler{" +
                "id=" + id +
                ", weeklyTaskName='" + weeklyTaskName + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", user=" + user +
                '}';
    }

    @Transient
    private String templateSelectorString;

    // this is for forms in frontend to have a readable String
    public void generateTemplateSelectorString() {
        String dayOfWeekStr = dayOfWeek
                .getDisplayName(TextStyle.FULL,
                        Locale.getDefault());
        this.templateSelectorString = weeklyTaskName
                + " (every " + dayOfWeekStr + ")";
    }

}
