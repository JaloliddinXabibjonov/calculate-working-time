package uz.devops.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import uz.devops.domain.enumeration.Status;

/**
 * A DTO for the {@link uz.devops.domain.WorkHistory} entity.
 */
public class WorkHistoryDTO implements Serializable {

    private Long id;

    private Instant start;

    private Instant toLunch;

    private Instant fromLunch;

    private Instant end;

    private String reasonDescription;

    private Status status;

    private WorkerDTO worker;

    private ReasonDTO reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getToLunch() {
        return toLunch;
    }

    public void setToLunch(Instant toLunch) {
        this.toLunch = toLunch;
    }

    public Instant getFromLunch() {
        return fromLunch;
    }

    public void setFromLunch(Instant fromLunch) {
        this.fromLunch = fromLunch;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public WorkerDTO getWorker() {
        return worker;
    }

    public void setWorker(WorkerDTO worker) {
        this.worker = worker;
    }

    public ReasonDTO getReason() {
        return reason;
    }

    public void setReason(ReasonDTO reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkHistoryDTO)) {
            return false;
        }

        WorkHistoryDTO workHistoryDTO = (WorkHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkHistoryDTO{" +
            "id=" + getId() +
            ", start='" + getStart() + "'" +
            ", toLunch='" + getToLunch() + "'" +
            ", fromLunch='" + getFromLunch() + "'" +
            ", end='" + getEnd() + "'" +
            ", reasonDescription='" + getReasonDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", worker=" + getWorker() +
            ", reason=" + getReason() +
            "}";
    }
}
