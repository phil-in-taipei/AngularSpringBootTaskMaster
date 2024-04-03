package backend.taskMaster.models.tasks.taskSchedulers;

import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;
import backend.taskMaster.models.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTaskScheduler {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String monthlyTaskName;

    @Column(nullable = false)
    private Integer dayOfMonth;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn
    private User user;

    // orphanRemoval deletes related quarterly applications
    // of the MonthlyTaskScheduler upon deletion
    @OneToMany(mappedBy = "monthlyTaskScheduler", orphanRemoval = true)
    private Set<MonthlyTaskAppliedQuarterly> monthlyTaskAppliedQuarterly;

    public MonthlyTaskScheduler(
            String monthlyTaskName,
            Integer dayOfMonth,
            User user
    ) {
        this.monthlyTaskName = monthlyTaskName;
        this.dayOfMonth = dayOfMonth;
        this.user = user;
    }


    @Override
    public String toString() {
        return "MonthlyTaskScheduler{" +
                "id=" + id +
                ", Monthly Task Name='" + monthlyTaskName + '\'' +
                ", Day Of Month=" + dayOfMonth +
                '}';
    }

    @Transient
    private String templateSelectorString;

    // this is for forms in frontend to have a readable String
    public void generateTemplateSelectorString() {
        String ordinalNumber = "th";
        if (dayOfMonth == 1 || dayOfMonth == 21 || dayOfMonth == 31) {
            ordinalNumber = "st";
        } if (dayOfMonth == 2 || dayOfMonth == 22) {
            ordinalNumber = "nd";
        } if (dayOfMonth == 3 || dayOfMonth == 23) {
            ordinalNumber = "rd";
        }
        this.templateSelectorString = monthlyTaskName
                + ": " + dayOfMonth
                + ordinalNumber +" day of month ";
    }

}
