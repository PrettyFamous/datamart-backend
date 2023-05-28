package su.vistar.datamart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Calendar;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@OnDelete(action = OnDeleteAction.CASCADE)
public class Fact {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_fact")
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String systemName;

    @NonNull
    @Column(name = "created_date", updatable = false)
    private Calendar createdDate;

    @NonNull
    @JoinColumn(name = "id_user")
    @ManyToOne
    private User user;
}
