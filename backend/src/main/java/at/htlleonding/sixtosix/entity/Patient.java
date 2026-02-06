package at.htlleonding.sixtosix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient Entity
 * Repräsentiert eine zu betreuende Person (Klient).
 */
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vorname darf nicht leer sein")
    @Column(nullable = false)
    private String firstname;

    @NotBlank(message = "Nachname darf nicht leer sein")
    @Column(nullable = false)
    private String lastname;

    @NotNull(message = "Geburtsdatum darf nicht null sein")
    @Past(message = "Geburtsdatum muss in der Vergangenheit liegen")
    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private Boolean deleted = false;

    // Relationen
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareAssignment> careAssignments = new ArrayList<>();

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Anamnesis anamnesis;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ADLAssessment> adlAssessments = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillAssessment> skillAssessments = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Intervention> interventions = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyPlan> dailyPlans = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medication> medications = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareNeed> careNeeds = new ArrayList<>();

    // Konstruktoren
    public Patient() {
    }

    public Patient(String firstname, String lastname, LocalDate birthdate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<CareAssignment> getCareAssignments() {
        return careAssignments;
    }

    public void setCareAssignments(List<CareAssignment> careAssignments) {
        this.careAssignments = careAssignments;
    }

    public Anamnesis getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(Anamnesis anamnesis) {
        this.anamnesis = anamnesis;
    }

    public List<ADLAssessment> getAdlAssessments() {
        return adlAssessments;
    }

    public void setAdlAssessments(List<ADLAssessment> adlAssessments) {
        this.adlAssessments = adlAssessments;
    }

    public List<SkillAssessment> getSkillAssessments() {
        return skillAssessments;
    }

    public void setSkillAssessments(List<SkillAssessment> skillAssessments) {
        this.skillAssessments = skillAssessments;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }

    public void setInterventions(List<Intervention> interventions) {
        this.interventions = interventions;
    }

    public List<DailyPlan> getDailyPlans() {
        return dailyPlans;
    }

    public void setDailyPlans(List<DailyPlan> dailyPlans) {
        this.dailyPlans = dailyPlans;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public List<CareNeed> getCareNeeds() {
        return careNeeds;
    }

    public void setCareNeeds(List<CareNeed> careNeeds) {
        this.careNeeds = careNeeds;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthdate=" + birthdate +
                ", deleted=" + deleted +
                '}';
    }
}

