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
 * Criteria class for the {@link uz.devops.domain.Reason} entity. This class is used
 * in {@link uz.devops.web.rest.ReasonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reasons?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ReasonCriteria implements Serializable, Criteria {

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

    private StatusFilter status;

    private LongFilter parentId;

    private LongFilter actionId;

    private LongFilter workHistoryId;

    private Boolean distinct;

    public ReasonCriteria() {}

    public ReasonCriteria(ReasonCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.parentId = other.parentId == null ? null : other.parentId.copy();
        this.actionId = other.actionId == null ? null : other.actionId.copy();
        this.workHistoryId = other.workHistoryId == null ? null : other.workHistoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ReasonCriteria copy() {
        return new ReasonCriteria(this);
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

    public LongFilter getParentId() {
        return parentId;
    }

    public LongFilter parentId() {
        if (parentId == null) {
            parentId = new LongFilter();
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getActionId() {
        return actionId;
    }

    public LongFilter actionId() {
        if (actionId == null) {
            actionId = new LongFilter();
        }
        return actionId;
    }

    public void setActionId(LongFilter actionId) {
        this.actionId = actionId;
    }

    public LongFilter getWorkHistoryId() {
        return workHistoryId;
    }

    public LongFilter workHistoryId() {
        if (workHistoryId == null) {
            workHistoryId = new LongFilter();
        }
        return workHistoryId;
    }

    public void setWorkHistoryId(LongFilter workHistoryId) {
        this.workHistoryId = workHistoryId;
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
        final ReasonCriteria that = (ReasonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(status, that.status) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(actionId, that.actionId) &&
            Objects.equals(workHistoryId, that.workHistoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, parentId, actionId, workHistoryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReasonCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (parentId != null ? "parentId=" + parentId + ", " : "") +
            (actionId != null ? "actionId=" + actionId + ", " : "") +
            (workHistoryId != null ? "workHistoryId=" + workHistoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
