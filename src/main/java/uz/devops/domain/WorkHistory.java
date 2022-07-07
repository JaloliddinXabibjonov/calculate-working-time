package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cascade;
import uz.devops.domain.enumeration.Status;

/**
 * A WorkHistory.
 */
@Entity
@Table(name = "work_history")
public class WorkHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start")
    private Instant start;

    @Column(name = "to_lunch")
    private Instant toLunch;

    @Column(name = "from_lunch")
    private Instant fromLunch;

    @Column(name = "jhi_end")
    private Instant end;

    @Column(name = "reason_description")
    private String reasonDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JsonIgnoreProperties(value = { "workHistories" }, allowSetters = true)
    private Worker worker;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parent", "action", "workHistories" }, allowSetters = true)
    private Reason reason;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public WorkHistory() {}

    public WorkHistory(Instant start, Status status, Worker worker) {
        this.start = start;
        this.status = status;
        this.worker = worker;
    }

    public Long getId() {
        return this.id;
    }

    public WorkHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStart() {
        return this.start;
    }

    public WorkHistory start(Instant start) {
        this.setStart(start);
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getToLunch() {
        return this.toLunch;
    }

    public WorkHistory toLunch(Instant toLunch) {
        this.setToLunch(toLunch);
        return this;
    }

    public void setToLunch(Instant toLunch) {
        this.toLunch = toLunch;
    }

    public Instant getFromLunch() {
        return this.fromLunch;
    }

    public WorkHistory fromLunch(Instant fromLunch) {
        this.setFromLunch(fromLunch);
        return this;
    }

    public void setFromLunch(Instant fromLunch) {
        this.fromLunch = fromLunch;
    }

    public Instant getEnd() {
        return this.end;
    }

    public WorkHistory end(Instant end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public String getReasonDescription() {
        return this.reasonDescription;
    }

    public WorkHistory reasonDescription(String reasonDescription) {
        this.setReasonDescription(reasonDescription);
        return this;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public Status getStatus() {
        return this.status;
    }

    public WorkHistory status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Worker getWorker() {
        return this.worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public WorkHistory worker(Worker worker) {
        this.setWorker(worker);
        return this;
    }

    public Reason getReason() {
        return this.reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public WorkHistory reason(Reason reason) {
        this.setReason(reason);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkHistory)) {
            return false;
        }
        return id != null && id.equals(((WorkHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkHistory{" +
            "id=" + getId() +
            ", start='" + getStart() + "'" +
            ", toLunch='" + getToLunch() + "'" +
            ", fromLunch='" + getFromLunch() + "'" +
            ", end='" + getEnd() + "'" +
            ", reasonDescription='" + getReasonDescription() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
