package uz.devops.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import uz.devops.domain.enumeration.Status;

/**
 * Criteria class for the {@link uz.devops.domain.Action} entity. This class is used
 * in {@link uz.devops.web.rest.ActionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /actions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ActionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private StringFilter command;

    private StatusFilter status;

    private LongFilter reasonId;

    private Boolean distinct;

    public ActionCriteria() {}

    public ActionCriteria(ActionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.command = other.command == null ? null : other.command.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.reasonId = other.reasonId == null ? null : other.reasonId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ActionCriteria copy() {
        return new ActionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getCommand() {
        return command;
    }

    public StringFilter command() {
        if (command == null) {
            command = new StringFilter();
        }
        return command;
    }

    public void setCommand(StringFilter command) {
        this.command = command;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public StatusFilter status() {
        if (status == null) {
            status = new StatusFilter();
        }
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    public LongFilter getReasonId() {
        return reasonId;
    }

    public LongFilter reasonId() {
        if (reasonId == null) {
            reasonId = new LongFilter();
        }
        return reasonId;
    }

    public void setReasonId(LongFilter reasonId) {
        this.reasonId = reasonId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ActionCriteria that = (ActionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(command, that.command) &&
            Objects.equals(status, that.status) &&
            Objects.equals(reasonId, that.reasonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, command, status, reasonId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (command != null ? "command=" + command + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (reasonId != null ? "reasonId=" + reasonId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
