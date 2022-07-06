package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import uz.devops.domain.enumeration.Status;

/**
 * A Action.
 */
@Entity
@Table(name = "action")
public class Action implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 25)
    @Column(name = "name", length = 25, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Size(max = 25)
    @Column(name = "command", length = 25, nullable = false)
    private String command;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @JsonIgnoreProperties(value = { "parent", "action", "workHistories" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Reason reason;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Action() {}

    public Action(String name, String command, Status status) {
        this.name = name;
        this.command = command;
        this.status = status;
    }

    public Action(String name, String command, Status status, Reason reason) {
        this.name = name;
        this.command = command;
        this.status = status;
        this.reason = reason;
    }

    public Long getId() {
        return this.id;
    }

    public Action id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Action name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Action description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommand() {
        return this.command;
    }

    public Action command(String command) {
        this.setCommand(command);
        return this;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Status getStatus() {
        return this.status;
    }

    public Action status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Reason getReason() {
        return this.reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Action reason(Reason reason) {
        this.setReason(reason);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Action)) {
            return false;
        }
        return id != null && id.equals(((Action) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Action{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", command='" + getCommand() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
