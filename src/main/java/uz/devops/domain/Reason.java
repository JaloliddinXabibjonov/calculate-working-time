package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import uz.devops.domain.enumeration.Status;

/**
 * A Reason.
 */
@Entity
@Table(name = "reason")
public class Reason implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parent", "action", "workHistories" }, allowSetters = true)
    private Reason parent;

    @JsonIgnoreProperties(value = { "reason" }, allowSetters = true)
    @OneToOne(mappedBy = "reason")
    private Action action;

    @OneToMany(mappedBy = "reason")
    @JsonIgnoreProperties(value = { "worker", "reason" }, allowSetters = true)
    private Set<WorkHistory> workHistories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Reason() {}

    public Reason(Long id, String name, Status status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public Reason id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Reason name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Reason description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public Reason status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Reason getParent() {
        return this.parent;
    }

    public void setParent(Reason reason) {
        this.parent = reason;
    }

    public Reason parent(Reason reason) {
        this.setParent(reason);
        return this;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        if (this.action != null) {
            this.action.setReason(null);
        }
        if (action != null) {
            action.setReason(this);
        }
        this.action = action;
    }

    public Reason action(Action action) {
        this.setAction(action);
        return this;
    }

    public Set<WorkHistory> getWorkHistories() {
        return this.workHistories;
    }

    public void setWorkHistories(Set<WorkHistory> workHistories) {
        if (this.workHistories != null) {
            this.workHistories.forEach(i -> i.setReason(null));
        }
        if (workHistories != null) {
            workHistories.forEach(i -> i.setReason(this));
        }
        this.workHistories = workHistories;
    }

    public Reason workHistories(Set<WorkHistory> workHistories) {
        this.setWorkHistories(workHistories);
        return this;
    }

    public Reason addWorkHistory(WorkHistory workHistory) {
        this.workHistories.add(workHistory);
        workHistory.setReason(this);
        return this;
    }

    public Reason removeWorkHistory(WorkHistory workHistory) {
        this.workHistories.remove(workHistory);
        workHistory.setReason(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reason)) {
            return false;
        }
        return id != null && id.equals(((Reason) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reason{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
