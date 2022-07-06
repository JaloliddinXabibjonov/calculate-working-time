package uz.devops.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import uz.devops.domain.enumeration.Status;

/**
 * A DTO for the {@link uz.devops.domain.Reason} entity.
 */
public class ReasonDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Status status;

    private ReasonDTO parent;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ReasonDTO getParent() {
        return parent;
    }

    public void setParent(ReasonDTO parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReasonDTO)) {
            return false;
        }

        ReasonDTO reasonDTO = (ReasonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reasonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReasonDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", parent=" + getParent() +
            "}";
    }
}
