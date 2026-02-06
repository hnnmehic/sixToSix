package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * ADL Definition Entity
 * ADL = Aktivitäten des täglichen Lebens
 * Vordefinierte Tätigkeiten des Alltags (z.B. Essen, Trinken, Mobilität).
 */
@Entity
@Table(name = "adl_definition")
public class ADLDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name darf nicht leer sein")
    @Column(nullable = false, unique = true)
    private String name;

    // Relationen
    @OneToMany(mappedBy = "adlDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ADLAssessment> assessments = new ArrayList<>();

    // Konstruktoren
    public ADLDefinition() {
    }

    public ADLDefinition(String name) {
        this.name = name;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ADLAssessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<ADLAssessment> assessments) {
        this.assessments = assessments;
    }

    @Override
    public String toString() {
        return "ADLDefinition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

