package uz.devops.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.domain.Worker} entity.
 */
public class WorkerDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 5)
    private String fullName;

    @NotNull
    private Long workerTgId;

    private String role;

    public WorkerDTO(String fullName, Long workerTgId, String role) {
        this.fullName = fullName;
        this.workerTgId = workerTgId;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getWorkerTgId() {
        return workerTgId;
    }

    public void setWorkerTgId(Long workerTgId) {
        this.workerTgId = workerTgId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkerDTO)) {
            return false;
        }

        WorkerDTO workerDTO = (WorkerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkerDTO{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", workerTgId=" + getWorkerTgId() +
            ", roles=" + getRole() +
            "}";
    }
}
