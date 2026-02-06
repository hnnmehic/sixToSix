package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Skill Definition Entity
 * Vordefinierte Fähigkeitsbereiche (z.B. Selbstfürsorge, Haushalt, Entspannung).
 * Analog zu ADLs bewertet.
 */
@Entity
@Table(name = "skill_definition")
public class SkillDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name darf nicht leer sein")
    @Column(nullable = false, unique = true)
    private String name;

    // Relationen
    @OneToMany(mappedBy = "skillDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillAssessment> assessments = new ArrayList<>();

    // Konstruktoren
    public SkillDefinition() {
    }

    public SkillDefinition(String name) {
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

    public List<SkillAssessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<SkillAssessment> assessments) {
        this.assessments = assessments;
    }

    @Override
    public String toString() {
        return "SkillDefinition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

