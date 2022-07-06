package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Worker.
 */
@Entity
@Table(name = "worker")
public class Worker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 5)
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "worker_tg_id", nullable = false)
    private Long workerTgId;

    @Column(name = "role")
    private String role;

    @Column(unique = true)
    private Long chatId;

    @OneToMany(mappedBy = "worker")
    @JsonIgnoreProperties(value = { "worker", "reason" }, allowSetters = true)
    private Set<WorkHistory> workHistories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Worker() {}

    public Worker(String fullName, Long workerTgId, String role) {
        this.fullName = fullName;
        this.workerTgId = workerTgId;
        this.role = role;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getId() {
        return this.id;
    }

    public Worker id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public Worker fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getWorkerTgId() {
        return this.workerTgId;
    }

    public Worker workerTgId(Long workerTgId) {
        this.setWorkerTgId(workerTgId);
        return this;
    }

    public void setWorkerTgId(Long workerTgId) {
        this.workerTgId = workerTgId;
    }

    public String getRole() {
        return this.role;
    }

    public Worker role(String role) {
        this.setRole(role);
        return this;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<WorkHistory> getWorkHistories() {
        return this.workHistories;
    }

    public void setWorkHistories(Set<WorkHistory> workHistories) {
        if (this.workHistories != null) {
            this.workHistories.forEach(i -> i.setWorker(null));
        }
        if (workHistories != null) {
            workHistories.forEach(i -> i.setWorker(this));
        }
        this.workHistories = workHistories;
    }

    public Worker workHistories(Set<WorkHistory> workHistories) {
        this.setWorkHistories(workHistories);
        return this;
    }

    public Worker addWorkHistory(WorkHistory workHistory) {
        this.workHistories.add(workHistory);
        workHistory.setWorker(this);
        return this;
    }

    public Worker removeWorkHistory(WorkHistory workHistory) {
        this.workHistories.remove(workHistory);
        workHistory.setWorker(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Worker)) {
            return false;
        }
        return id != null && id.equals(((Worker) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Worker{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", workerTgId=" + getWorkerTgId() +
            ", role='" + getRole() + "'" +
            "}";
    }
}
