package su.vistar.datamart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@Setter
@Table(name="fact_attr")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class FactAttr {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_fact_attr")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "system_name")
    private String systemName;


    @NonNull
    @JoinColumn(name = "id_type")
    @ManyToOne
    private Type type;

    @NonNull
    @JoinColumn(name = "id_fact")
    @ManyToOne
    private Fact fact;
}
