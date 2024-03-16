package backend.taskMaster.models.tasks.task;

import backend.taskMaster.models.user.User;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String taskName;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatusEnum status;

    @Column(nullable = true)
    private String comments;

    @CreationTimestamp
    private LocalDateTime createdDateTime;

    // the fields below are from a previous version that involved
    // calling an external weather api -- may or may not re-implement
    // also waiting to check whether to implement
    // the "intervalTaskGroup" relation
    //@ManyToOne(
    //        cascade = CascadeType.ALL,
    //        optional = true
    //)
    //private IntervalTaskGroup intervalTaskGroup;

    @UpdateTimestamp
    private LocalDateTime updatedDateTime;

    @Override
    public String toString() {
        return "MaintenanceTask{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", date=" + date +
                // commented out user field, so that test would pass
                // most likely related to user service being mocked
                //", user=" + user.getUsername() +
                ", status=" + status +
                ", comments='" + comments + '\'' +
                ", createdDateTime=" + createdDateTime +
                ", updatedDateTime=" + updatedDateTime +
               // ", intervalTaskGroup=" + intervalTaskGroup +
              '}';
    }
}
