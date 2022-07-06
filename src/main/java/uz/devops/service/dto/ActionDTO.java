package uz.devops.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import uz.devops.domain.enumeration.Status;

/**
 * A DTO for the {@link uz.devops.domain.Action} entity.
 */
public class ActionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 25)
    private String name;

    private String description;

    @NotNull
    @Size(max = 25)
    private String command;

    @NotNull
    private Status status;

    private ReasonDTO reason;

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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        if (!(o instanceof ActionDTO)) {
            return false;
        }

        ActionDTO actionDTO = (ActionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, actionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", command='" + getCommand() + "'" +
            ", status='" + getStatus() + "'" +
            ", reason=" + getReason() +
            "}";
    }
}
